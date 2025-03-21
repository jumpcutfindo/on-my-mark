package com.jumpcutfindo.onmymark.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.party.exceptions.*;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyManager {
    private final List<PartyMember> partyMembers;
    private final List<Party> parties;
    private final List<PartyInvite> partyInvites;

    public PartyManager() {
        this.partyMembers = new ArrayList<>();
        this.parties = new ArrayList<>();
        this.partyInvites = new ArrayList<>();
    }

    public Party createParty(ServerPlayerEntity creator, String name) {
        PartyMember partyLeader = this.getOrCreate(creator);

        Party party = new Party(name, partyLeader);
        this.parties.add(party);

        partyLeader.setCurrentParty(party);
        party.addPartyMember(partyLeader);

        OnMyMarkMod.LOGGER.info("\"{}\" created a new party \"{}\"", partyLeader.displayName(), party.partyName());

        return party;
    }

    public void addPlayerToParty(UUID partyId, ServerPlayerEntity leader, ServerPlayerEntity player) throws PartyNotFoundException, InvalidPartyPermissionsException, PlayerAlreadyInPartyException {
        Party party = getPartyById(partyId);

        PartyMember partyLeader = this.getOrCreate(leader);
        if (!party.isPartyLeader(partyLeader)) {
            throw new InvalidPartyPermissionsException(partyLeader.displayName());
        }

        PartyMember partyMember = this.getOrCreate(player);
        if (partyMember.state() == PartyMember.State.IN_PARTY) {
            throw new PlayerAlreadyInPartyException(partyMember.displayName());
        }

        party.addPartyMember(partyMember);
        partyMember.setCurrentParty(party);
    }

    public void removePlayerFromParty(UUID partyId, ServerPlayerEntity leader, ServerPlayerEntity player) throws PartyNotFoundException, InvalidPartyPermissionsException, RemovePartyLeaderException {
        Party party = getPartyById(partyId);

        PartyMember partyLeader = this.getOrCreate(leader);
        PartyMember partyMember = this.getOrCreate(leader);

        if (!party.isPartyLeader(partyLeader)) {
            throw new InvalidPartyPermissionsException(partyLeader.displayName());
        }

        if (party.isPartyLeader(partyMember)) {
            throw new RemovePartyLeaderException(partyMember.displayName());
        }

        party.removePartyMember(partyMember);
        partyMember.removeCurrentParty();
    }

    public void leaveParty(ServerPlayerEntity player) {
        PartyMember partyMember = this.getOrCreate(player);

        if (partyMember.state() == PartyMember.State.AVAILABLE) {
            // Party member wasn't in party, just return
            return;
        }

        Party party = partyMember.currentParty();

        party.removePartyMember(partyMember);
        partyMember.removeCurrentParty();

        OnMyMarkMod.LOGGER.info("\"{}\" left party \"{}\"", partyMember.displayName(), party.partyName());

        // Disband party if there are no members left
        if (party.partyMembers().isEmpty()) {
            this.disbandParty(party);
            OnMyMarkMod.LOGGER.info("\"{}\" was disbanded as last player left", party.partyName());
        }
    }

    public void createInvite(UUID partyId, ServerPlayerEntity leader, ServerPlayerEntity player) throws PartyNotFoundException, InvalidPartyPermissionsException {
        PartyMember partyLeader = this.getOrCreate(leader);
        PartyMember invitee = this.getOrCreate(player);

        Party party = this.getPartyById(partyId);

        if (!party.isPartyLeader(partyLeader)) {
            throw new InvalidPartyPermissionsException(partyLeader.displayName());
        }

        this.partyInvites.add(new PartyInvite(party, partyLeader, invitee));
    }

    public void acceptInvite(ServerPlayerEntity player) throws PartyInviteNotFoundException, PartyNotFoundException, PlayerAlreadyInPartyException, InvalidPartyPermissionsException {
        PartyMember invitee = this.getOrCreate(player);

        Optional<PartyInvite> piOpt = this.partyInvites.stream()
                .filter(partyInvite -> partyInvite.to().equals(invitee))
                .findFirst();

        if (piOpt.isEmpty()) {
            throw new PartyInviteNotFoundException(invitee.displayName());
        }

        PartyInvite partyInvite = piOpt.get();
        this.partyInvites.remove(partyInvite);

        this.addPlayerToParty(partyInvite.party().partyId(), (ServerPlayerEntity) partyInvite.from().player(), (ServerPlayerEntity) partyInvite.to().player());
    }

    public void rejectInvite(ServerPlayerEntity player) throws PartyInviteNotFoundException {
        PartyMember invitee = this.getOrCreate(player);

        Optional<PartyInvite> piOpt = this.partyInvites.stream()
                .filter(partyInvite -> partyInvite.to().equals(invitee))
                .findFirst();

        if (piOpt.isEmpty()) {
            throw new PartyInviteNotFoundException(invitee.displayName());
        }

        PartyInvite partyInvite = piOpt.get();
        this.partyInvites.remove(partyInvite);
    }

    private void disbandParty(Party party) {
        this.parties.remove(party);
    }

    public Party getPartyById(UUID partyId) throws PartyNotFoundException {
        Optional<Party> partyOpt = this.parties.stream()
                .filter((party) -> party.partyId().equals(partyId))
                .findFirst();

        if (partyOpt.isEmpty()) {
            throw new PartyNotFoundException(partyId);
        }

        return partyOpt.get();
    }

    private PartyMember getOrCreate(ServerPlayerEntity player) {
        Optional<PartyMember> partyMemberOpt = this.partyMembers.stream()
                .filter(pm -> pm.player().equals(player))
                .findFirst();

        if (partyMemberOpt.isEmpty()) {
            PartyMember partyMember = new PartyMember(player);
            this.partyMembers.add(partyMember);
            return partyMember;
        } else {
            return partyMemberOpt.get();
        }
    }
}
