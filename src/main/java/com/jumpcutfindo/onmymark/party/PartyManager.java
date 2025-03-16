package com.jumpcutfindo.onmymark.party;

import com.jumpcutfindo.onmymark.exceptions.InvalidPartyPermissionsException;
import com.jumpcutfindo.onmymark.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.exceptions.PlayerAlreadyInPartyException;
import com.jumpcutfindo.onmymark.exceptions.RemovePartyLeaderException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyManager {
    private final ServerWorld serverWorld;

    private final List<PartyMember> partyMembers;
    private final List<Party> parties;

    public PartyManager(ServerWorld serverWorld) {
        this.serverWorld = serverWorld;

        this.partyMembers = new ArrayList<>();
        this.parties = new ArrayList<>();
    }

    public Party createParty(ServerPlayerEntity creator, String name) {
        PartyMember partyLeader = this.getOrCreate(creator);

        Party party = new Party(name, partyLeader);
        this.parties.add(party);

        partyLeader.setCurrentParty(party);

        return party;
    }

    public void addPlayerToParty(UUID partyId, ServerPlayerEntity leader, ServerPlayerEntity player) throws PartyNotFoundException, InvalidPartyPermissionsException, PlayerAlreadyInPartyException {
        Party party = getPartyById(partyId);

        PartyMember partyLeader = this.getOrCreate(player);
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
        Optional<PartyMember> partyMember = this.partyMembers.stream()
                .filter(pm -> pm.player().equals(player))
                .findFirst();

        return partyMember.orElseGet(() -> new PartyMember(player));
    }
}
