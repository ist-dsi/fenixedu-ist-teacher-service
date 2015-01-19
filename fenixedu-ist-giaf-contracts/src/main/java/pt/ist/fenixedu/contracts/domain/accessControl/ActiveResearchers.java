/**
 * Copyright © 2011 Instituto Superior Técnico
 *
 * This file is part of FenixEdu GIAF Contracts.
 *
 * FenixEdu GIAF Contracts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu GIAF Contracts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu GIAF Contracts.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.contracts.domain.accessControl;

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.GroupStrategy;
import org.joda.time.DateTime;

import pt.ist.fenixedu.contracts.domain.research.Researcher;

@GroupOperator("activeResearchers")
public class ActiveResearchers extends GroupStrategy {
    private static final long serialVersionUID = -6648971466827719165L;

    @Override
    public String getPresentationName() {
        return "Active Researchers";
    }

    @Override
    public Set<User> getMembers() {
        return Bennu.getInstance().getResearchersSet().stream().filter(Researcher::isActiveContractedResearcher)
                .map(researcher -> researcher.getPerson().getUser()).collect(Collectors.toSet());
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user) {
        return user != null && user.getPerson() != null && user.getPerson().getResearcher() != null
                && user.getPerson().getResearcher().isActiveContractedResearcher();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

}
