package com.jumpcutfindo.onmymark.party;

import java.util.function.Predicate;

public class PartyMemberFilters {
    public static class SameDimensionFilter implements Predicate<ServerPartyMember> {
        private final ServerPartyMember mainMember;

        public SameDimensionFilter(ServerPartyMember mainMember) {
            this.mainMember = mainMember;
        }

        @Override
        public boolean test(ServerPartyMember partyMember) {
            return mainMember.player().getWorld().getRegistryKey().equals(partyMember.player().getWorld().getRegistryKey());
        }
    }
}
