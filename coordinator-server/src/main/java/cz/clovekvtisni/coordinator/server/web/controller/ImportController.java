package cz.clovekvtisni.coordinator.server.web.controller;

import au.com.bytecode.opencsv.CSVReader;
import cz.clovekvtisni.coordinator.server.web.model.ImportFileForm;
import cz.clovekvtisni.coordinator.server.web.model.ImportUsersForm;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;


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
        return "admin/import-file-form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onFormSubmit(HttpServletRequest request, Model model) {

        ImportUsersForm form = new ImportUsersForm();
        populateForm(form, request);

        if (form.getRowCount() == 0) {
            //bindingResult.addError(new ObjectError("global", "empty csv file"));

        } else {
            model.addAttribute("form", form);
        }

        return "admin/import-data-form";
    }

    @ModelAttribute("colTypes")
    public Map<String, String> colTypes() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("UserEntity.firstName", getMessage("UserEntity.firstName"));
        map.put("UserEntity.lastName", getMessage("UserEntity.lastName"));
        map.put("UserEntity.email", getMessage("UserEntity.email"));
        map.put("UserEntity.phone", getMessage("UserEntity.phone"));
        map.put("UserEntity.birthday", getMessage("UserEntity.birthday"));
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
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream), ';', '"');
                    form.setVal(reader.readAll().toArray(new String[0][0]));

                } else if (name.equals("organizationId")) {
                    form.setOrganizationId(readStream(inputStream));

                } else if (name.equals("eventId")) {
                    String data = readStream(inputStream);
                    form.setEventId("".equals(data) ? null : Long.parseLong(data));
                } else {
                    readStream(inputStream);
                }
            }

            return null;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    private String readStream(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
