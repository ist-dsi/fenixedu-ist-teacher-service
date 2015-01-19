/**
 * Copyright © 2011 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Teacher Credits.
 *
 * FenixEdu Teacher Credits is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Teacher Credits is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Teacher Credits.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.teacher.ui.struts.action.credits.departmentMember;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.struts.annotations.ExceptionHandling;
import org.fenixedu.bennu.struts.annotations.Exceptions;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

import pt.ist.fenixedu.teacher.domain.SupportLesson;
import pt.ist.fenixedu.teacher.ui.struts.action.credits.ManageTeacherSupportLessonsDispatchAction;
import pt.ist.fenixedu.teacher.ui.struts.action.credits.ManageTeacherSupportLessonsDispatchAction.InvalidPeriodException;
import pt.ist.fenixframework.FenixFramework;

@Mapping(module = "departmentMember", path = "/supportLessonsManagement",
        input = "/supportLessonsManagement.do?method=prepareEdit&page=0", formBean = "supportLessonForm",
        functionality = DepartmentMemberViewTeacherCreditsDA.class)
@Forwards({
        @Forward(name = "successfull-delete",
                path = "/departmentMember/degreeTeachingServiceManagement.do?method=showTeachingServiceDetails"),
        @Forward(name = "successfull-edit",
                path = "/departmentMember/degreeTeachingServiceManagement.do?method=showTeachingServiceDetails"),
        @Forward(name = "edit-support-lesson", path = "/credits/supportLessons/editSupportLesson.jsp"),
        @Forward(name = "list-support-lessons",
                path = "/departmentMember/degreeTeachingServiceManagement.do?method=showTeachingServiceDetails"),
        @Forward(name = "teacher-not-found", path = "/departmentMember/credits.do?method=viewAnnualTeachingCredits") })
@Exceptions(value = {
        @ExceptionHandling(type = InvalidPeriodException.class, key = "message.invalidPeriod",
                handler = org.apache.struts.action.ExceptionHandler.class,
                path = "/supportLessonsManagement.do?method=prepareEdit&page=0", scope = "request"),
        @ExceptionHandling(type = org.fenixedu.academic.domain.exceptions.DomainException.class,
                handler = org.fenixedu.academic.ui.struts.config.FenixDomainExceptionHandler.class, scope = "request") })
public class DepartmentMemberManageTeacherSupportLessonsDispatchAction extends ManageTeacherSupportLessonsDispatchAction {

    public ActionForward prepareEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws NumberFormatException, FenixServiceException {

        DynaActionForm supportLessonForm = (DynaActionForm) form;
        String supportLesssonID = (String) supportLessonForm.get("supportLessonID");

        Professorship professorship = getDomainObject(supportLessonForm, "professorshipID");

        if (professorship == null || professorship.getTeacher() != getLoggedTeacher(request)) {
            createNewActionMessage(request);
            return mapping.findForward("teacher-not-found");
        }

        SupportLesson supportLesson = null;
        if (!StringUtils.isEmpty(supportLesssonID)) {
            supportLesson = FenixFramework.getDomainObject(supportLesssonID);
            if (!professorship.getSupportLessonsSet().contains(supportLesson)) {
                createNewActionMessage(request);
                return mapping.findForward("teacher-not-found");
            }
        }

        prepareToEdit(supportLesson, professorship, supportLessonForm, request);
        return mapping.findForward("edit-support-lesson");
    }

    private void createNewActionMessage(HttpServletRequest request) {
        ActionMessages actionMessages = new ActionMessages();
        actionMessages.add("", new ActionMessage("message.invalid.teacher"));
        saveMessages(request, actionMessages);
    }

    private Teacher getLoggedTeacher(HttpServletRequest request) {
        User userView = Authenticate.getUser();
        return userView.getPerson().getTeacher();
    }

    public ActionForward editSupportLesson(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws NumberFormatException, FenixServiceException, InvalidPeriodException {

        editSupportLesson(form, request);
        return mapping.findForward("successfull-edit");
    }

    public ActionForward deleteSupportLesson(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws NumberFormatException, FenixServiceException {

        deleteSupportLesson(request, form);
        return mapping.findForward("successfull-delete");
    }
}
