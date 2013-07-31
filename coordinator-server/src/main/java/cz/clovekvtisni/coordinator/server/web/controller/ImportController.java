package cz.clovekvtisni.coordinator.server.web.controller;

import au.com.bytecode.opencsv.CSVReader;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.UniqueKeyViolation;
import cz.clovekvtisni.coordinator.server.web.model.ImportFileForm;
import cz.clovekvtisni.coordinator.server.web.model.ImportUsersForm;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 21.12.12
 */
@Controller
@RequestMapping("/admin/import")
public class ImportController extends AbstractController {

    @RequestMapping(method = RequestMethod.GET)
    public String showForm(@ModelAttribute("importFileForm") @Valid ImportFileForm form, BindingResult bindingResult, Model model) {
        model.addAttribute("isValid", !bindingResult.hasErrors());
        model.addAttribute("disableMap", true);
        return "admin/import-file-form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onPostCsvFile(@ModelAttribute("importFileForm") ImportFileForm fileForm, HttpServletRequest request, BindingResult bindingResult, Model model) {

        ImportUsersForm form = new ImportUsersForm();

        form.setEventId(fileForm.getEventId());
        form.setOrganizationId(fileForm.getOrganizationId());
        form.setCharset(fileForm.getCharset());
        populateForm(form, request);
        form.preSelectTypes(colTypes().keySet());

        if (form.getRowCount() == 0) {
            model.addAttribute("disableMap", true);
            addFormError(bindingResult, "error.emptyCsvFile");
            fileForm.setEventId(form.getEventId());
            fileForm.setOrganizationId(form.getOrganizationId());
            fileForm.setCharset(form.getCharset());
            model.addAttribute("importFileForm", fileForm);

            return "admin/import-file-form";

        } else if (form.getOrganizationId() == null) {
            throw NotFoundException.idNotExist("organization", null);

        } else {
            model.addAttribute("form", form);
        }

        model.addAttribute("disableMap", true);
        return "admin/import-data-form";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data")
    public String onPostData(@ModelAttribute("form") @Valid ImportUsersForm usersForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("disableMap", true);
            return "admin/import-data-form";
        }

        usersForm.setEventId(appContext.getActiveEvent().getId());
        List<String> types = usersForm.getTyp();
        List<Integer> checked = usersForm.getChecked();
        List<List<String>> vals = usersForm.getVal();
        Map<Integer, String> errorMap = new HashMap<Integer, String>();
        List<List<String>> errorVals = new ArrayList<List<String>>();
        for (int i = 0; i < vals.size(); i++) {
            if (!checked.contains(i))
                continue;
            List<String> row = vals.get(i);
            UserEntity user = fetchUser(types, row);
            if (user == null)
                continue;
            user.setOrganizationId(usersForm.getOrganizationId());
            UserInEventEntity inEvent = new UserInEventEntity();
            inEvent.setEventId(usersForm.getEventId());
            try {
                userService.register(user, inEvent, UserService.FLAG_FORCE_REGISTRATION);

            } catch (UniqueKeyViolation e) {
                errorVals.add(row);
                if (e.getProperty() == UniqueIndexEntity.Property.EMAIL) {
                    errorMap.put(errorVals.size() - 1, "error.EMAIL_VIOLATION");

                } else
                    errorMap.put(errorVals.size() - 1, "error.UNIQUE_KEY_VIOLATION");

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                errorVals.add(row);
                errorMap.put(errorVals.size() - 1, "error.unknown");
            }
        }

        if (errorMap.size() > 0) {
            usersForm.setVal(errorVals);
            checked = new ArrayList<Integer>(vals.size());
            for (int i = 0 ; i < vals.size() ; i++)
                checked.add(i);
            usersForm.setChecked(checked);
            model.addAttribute("errorMap", errorMap);
            model.addAttribute("disableMap", true);
            return "admin/import-data-form";

        } else {
            return "redirect:/admin/event/user/list?eventId=" + usersForm.getEventId();
        }
    }

    private UserEntity fetchUser(List<String> types, List<String> row) {
        if (types.size() != row.size())
            return null;
        UserEntity user = new UserEntity();
        boolean isValid = false;
        for (int i = 0; i < types.size(); i++) {
            String typeName = types.get(i);
            if (typeName == null)
                continue;
            String value = row.get(i);
            if ("UserEntity.firstName".equals(typeName)) {
                user.setFirstName(value);
                isValid = true;

            } else if ("UserEntity.lastName".equals(typeName)) {
                user.setLastName(value);
                isValid = true;

            } else if ("UserEntity.email".equals(typeName)) {
                user.setEmail(value);
                isValid = true;

            } else if ("UserEntity.phone".equals(typeName)) {
                user.setPhone(value);
                isValid = true;

                /*
                } else if ("UserEntity.birthday".equals(typeName)) {
                    // TODO
                */

            } else if ("UserEntity.addressLine".equals(typeName)) {
                user.setAddressLine(value);
                isValid = true;

            } else if ("UserEntity.city".equals(typeName)) {
                user.setCity(value);
                isValid = true;

            } else if ("UserEntity.zip".equals(typeName)) {
                user.setZip(value);
                isValid = true;

            } else if ("UserEntity.country".equals(typeName)) {
                user.setCountry(value);
                isValid = true;
            }
        }
        return isValid ?  user : null;
    }

    @ModelAttribute("colTypes")
    public Map<String, String> colTypes() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(ImportUsersForm.EMPTY_TYPE, getMessage("label.ignored"));
        map.put("UserEntity.firstName", getMessage("UserEntity.firstName"));
        map.put("UserEntity.lastName", getMessage("UserEntity.lastName"));
        map.put("UserEntity.email", getMessage("UserEntity.email"));
        map.put("UserEntity.phone", getMessage("UserEntity.phone"));
        //map.put("UserEntity.birthday", getMessage("UserEntity.birthday"));
        map.put("UserEntity.addressLine", getMessage("UserEntity.addressLine"));
        map.put("UserEntity.city", getMessage("UserEntity.city"));
        map.put("UserEntity.zip", getMessage("UserEntity.zip"));
        map.put("UserEntity.country", getMessage("UserEntity.country"));

        return map;
    }

    private InputStream populateForm(ImportUsersForm form, HttpServletRequest request) {
        try {
            ServletFileUpload upload = new ServletFileUpload();

            FileItemIterator iterator = upload.getItemIterator(request);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                String name = item.getFieldName();
                InputStream inputStream = item.openStream();

                if (!item.isFormField() && name.equals("csvFile")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream, form.getCharset()), ';', '"');
                    List<String[]> data = reader.readAll();
                    List<List<String>> vals = new ArrayList<List<String>>();
                    for (String[] cells : data) {
                        vals.add(Arrays.asList(cells));
                    }
                    form.setVal(vals);
                    List<Integer> checked = new ArrayList<Integer>(vals.size());
                    for (int i = 0 ; i < vals.size() ; i++)
                        checked.add(i);
                    form.setChecked(checked);

                } else if (name.equals("organizationId")) {
                    form.setOrganizationId(readStream(inputStream));

                } else if (name.equals("eventId")) {
                    String data = readStream(inputStream);
                    form.setEventId("".equals(data) ? null : Long.parseLong(data));

                } else if (name.equals("charset")) {
                    String data = readStream(inputStream);
                    form.setCharset("".equals(data) ? null : data);

                } else {
                    readStream(inputStream);
                }
            }

            return null;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    private String readStream(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
