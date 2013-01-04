package cz.clovekvtisni.coordinator.server.web.controller;

import au.com.bytecode.opencsv.CSVReader;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
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
        return "admin/import-file-form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onPostCsvFile(@ModelAttribute("importFileForm") ImportFileForm fileForm, HttpServletRequest request, BindingResult bindingResult, Model model) {

        ImportUsersForm form = new ImportUsersForm();
        populateForm(form, request);

        if (form.getRowCount() == 0) {
            addFormError(bindingResult, "error.emptyCsvFile");

        } else if (form.getOrganizationId() == null) {
            throw NotFoundException.idNotExist("organization", null);

        } else {
            model.addAttribute("form", form);
        }

        return "admin/import-data-form";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data")
    public String onPostData(@ModelAttribute("importUsersForm") @Valid ImportUsersForm usersForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/import-data-form";
        }

        return "admin/user/list";
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
