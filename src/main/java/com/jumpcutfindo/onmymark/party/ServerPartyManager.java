package com.jumpcutfindo.onmymark.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.party.exceptions.*;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServerPartyManager {
    private final List<ServerPartyMember> partyMembers;
    private final List<Party<ServerPartyMember>> parties;
    private final List<PartyInvite<ServerPartyMember>> partyInvites;

    public ServerPartyManager() {
        this.partyMembers = new ArrayList<>();
        this.parties = new ArrayList<>();
        this.partyInvites = new ArrayList<>();
    }

    public Party<ServerPartyMember> createParty(ServerPlayerEntity creator, String name) throws AlreadyInPartyException {
        ServerPartyMember partyLeader = this.getOrCreatePlayer(creator);

        if (partyLeader.isInParty()) {
            throw new AlreadyInPartyException(partyLeader.displayName());
        }

        Party<ServerPartyMember> party = new Party<>(name, partyLeader);
        this.parties.add(party);

        partyLeader.setCurrentParty(party);
        partyLeader.setState(PartyMember.State.IN_PARTY);
        party.addPartyMember(partyLeader);

        OnMyMarkMod.LOGGER.info("\"{}\" created a new party \"{}\"", partyLeader.displayName(), party.partyName());

        return party;
    }

    public void addPlayerToParty(UUID partyId, ServerPlayerEntity leader, ServerPlayerEntity player) throws PartyNotFoundException, InvalidPartyPermissionsException, PlayerAlreadyInPartyException {
        Party<ServerPartyMember> party = getPartyById(partyId);

        ServerPartyMember partyLeader = this.getOrCreatePlayer(leader);
        if (!party.isPartyLeader(partyLeader)) {
            throw new InvalidPartyPermissionsException(partyLeader.displayName());
        }

        ServerPartyMember partyMember = this.getOrCreatePlayer(player);
        if (partyMember.state() == PartyMember.State.IN_PARTY) {
            throw new PlayerAlreadyInPartyException(partyMember.displayName());
        }

        party.addPartyMember(partyMember);
        partyMember.setCurrentParty(party);
        partyMember.setState(PartyMember.State.IN_PARTY);
    }

    public void removePlayerFromParty(UUID partyId, ServerPlayerEntity leader, ServerPlayerEntity player) throws PartyNotFoundException, InvalidPartyPermissionsException, RemovePartyLeaderException, PlayerNotInPartyException {
        Party<ServerPartyMember> party = getPartyById(partyId);

        ServerPartyMember partyLeader = this.getOrCreatePlayer(leader);
        ServerPartyMember partyMember = this.getOrCreatePlayer(player);

        if (!party.isPartyLeader(partyLeader)) {
            throw new InvalidPartyPermissionsException(partyLeader.displayName());
        }

        if (party.isPartyLeader(partyMember)) {
            throw new RemovePartyLeaderException(partyMember.displayName());
        }

        if (!party.hasMember(partyMember)) {
            throw new PlayerNotInPartyException(partyMember.displayName());
        }

        party.removePartyMember(partyMember);
        partyMember.removeCurrentParty();
    }

    public Party<ServerPartyMember> leaveParty(ServerPlayerEntity player) {
        ServerPartyMember partyMember = this.getOrCreatePlayer(player);
        boolean isPartyLeader = partyMember.isPartyLeader();

        Party<ServerPartyMember> party = partyMember.currentParty();

        party.removePartyMember(partyMember);
        partyMember.removeCurrentParty();

        OnMyMarkMod.LOGGER.info("\"{}\" left party \"{}\"", partyMember.displayName(), party.partyName());

        // Disband party if:
        // - Member who left is the party leader
        // - There are no members left
        if (isPartyLeader || party.partyMembers().isEmpty()) {
            this.disbandParty(party);
        }

        return party;
    }

    public void createInvite(UUID partyId, ServerPlayerEntity leader, ServerPlayerEntity player) throws PartyNotFoundException, InvalidPartyPermissionsException, ExistingInviteException, AlreadyInPartyException {
        ServerPartyMember partyLeader = this.getOrCreatePlayer(leader);
        ServerPartyMember invitee = this.getOrCreatePlayer(player);

        Party<ServerPartyMember> party = this.getPartyById(partyId);

        if (!party.isPartyLeader(partyLeader)) {
            throw new InvalidPartyPermissionsException(partyLeader.displayName());
        }

        if (invitee.currentParty() != null) {
            throw new AlreadyInPartyException(invitee.displayName());
        }

        if (this.hasExistingInvite(invitee)) {
            throw new ExistingInviteException(invitee.displayName());
        }

        this.partyInvites.add(new PartyInvite<>(party, partyLeader, invitee));
    }

    public Party<ServerPartyMember> acceptInvite(ServerPlayerEntity player) throws PartyInviteNotFoundException, PartyNotFoundException, PlayerAlreadyInPartyException, InvalidPartyPermissionsException {
        ServerPartyMember invitee = this.getOrCreatePlayer(player);

        Optional<PartyInvite<ServerPartyMember>> piOpt = this.partyInvites.stream()
                .filter(partyInvite -> partyInvite.to().equals(invitee))
                .findFirst();

        if (piOpt.isEmpty()) {
            throw new PartyInviteNotFoundException(invitee.displayName());
        }

        PartyInvite<ServerPartyMember> partyInvite = piOpt.get();
        ServerPartyMember partyLeader = (ServerPartyMember) partyInvite.from();

        this.partyInvites.remove(partyInvite);

        this.addPlayerToParty(partyInvite.party().partyId(), partyLeader.player(), invitee.player());

        return partyInvite.party();
    }

    public Party<ServerPartyMember> rejectInvite(ServerPlayerEntity player) throws PartyInviteNotFoundException {
        PartyMember invitee = this.getOrCreatePlayer(player);
        PartyInvite<ServerPartyMember> existingInvite = this.getInviteOfInvitee(invitee);

        if (existingInvite == null) {
            throw new PartyInviteNotFoundException(invitee.displayName());
        }

        this.partyInvites.remove(existingInvite);

        return existingInvite.party();
    }

    public void removeInvite(ServerPlayerEntity player) {
        PartyMember invitee = this.getOrCreatePlayer(player);
        PartyInvite<ServerPartyMember> existingInvite = this.getInviteOfInvitee(invitee);

        if (existingInvite == null) {
            return;
        }

        this.partyInvites.remove(existingInvite);
    }

    private PartyInvite<ServerPartyMember> getInviteOfInvitee(PartyMember invitee) {
        Optional<PartyInvite<ServerPartyMember>> piOpt = this.partyInvites.stream()
                .filter(partyInvite -> partyInvite.to().equals(invitee))
                .findFirst();

        return piOpt.orElse(null);
    }

    public boolean hasExistingInvite(PartyMember invitee) {
        Optional<PartyInvite<ServerPartyMember>> piOpt = this.partyInvites.stream()
                .filter(partyInvite -> partyInvite.to().equals(invitee))
                .findFirst();

        return piOpt.isPresent();
    }

    public Party<ServerPartyMember> handlePlayerConnected(ServerPlayerEntity player) throws PlayerNotInPartyException {
        ServerPartyMember partyMember = this.getOrCreatePlayer(player);

        // Update player as the old reference points to a different object
        partyMember.setPlayer(player);

        if (partyMember.isInParty()) {
            partyMember.setState(PartyMember.State.IN_PARTY);
            return partyMember.currentParty();
        }

        throw new PlayerNotInPartyException(player.getDisplayName().getString());
    }

    public Party<ServerPartyMember> handlePlayerDisconnected(ServerPlayerEntity player) throws PlayerNotInPartyException {
        ServerPartyMember partyMember = this.getOrCreatePlayer(player);

        if (!partyMember.isInParty()) {
            throw new PlayerNotInPartyException(player.getDisplayName().getString());
        }

        partyMember.setState(PartyMember.State.OFFLINE);

        // Remove any existing invites as well for the player
        this.removeInvite(player);

        return partyMember.currentParty();
    }

    private void disbandParty(Party<ServerPartyMember> party) {
        party.setState(Party.State.DISBANDED);
        this.parties.remove(party);
    }

    public Party<ServerPartyMember> getPartyById(UUID partyId) throws PartyNotFoundException {
        Optional<Party<ServerPartyMember>> partyOpt = this.parties.stream()
                .filter((party) -> party.partyId().equals(partyId))
                .findFirst();

        if (partyOpt.isEmpty()) {
            throw new PartyNotFoundException(partyId);
        }

        return partyOpt.get();
    }

    public Party<ServerPartyMember> getPartyOfPlayer(ServerPlayerEntity player) throws PartyNotFoundException {
        Party<ServerPartyMember> party = this.getOrCreatePlayer(player).currentParty();

        if (party == null) {
            throw new PartyNotFoundException();
        }

        return this.getOrCreatePlayer(player).currentParty();
    }

    public ServerPartyMember getOrCreatePlayer(ServerPlayerEntity player) {
        Optional<ServerPartyMember> partyMemberOpt = this.partyMembers.stream()
                .filter(pm -> pm.id().equals(player.getUuid()))
                .findFirst();

        if (partyMemberOpt.isEmpty()) {
            ServerPartyMember partyMember = new ServerPartyMember(player);
            this.partyMembers.add(partyMember);
            return partyMember;
        } else {
            return partyMemberOpt.get();
        }
    }
}
