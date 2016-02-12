package com.adobe.ugc.srp

import com.adobe.ugc.srp.entities.CustomerEntity
import com.adobe.ugc.srp.exceptions.RepositoryException
import com.adobe.ugc.srp.repositories.CustomerRepository
import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet
import org.apache.sling.commons.json.JSONObject

@Slf4j
@SlingServlet(
    methods = [ "GET" ],
    paths = [ "/bin/adobe/ugc/srp"],
    extensions = [ "json" ]
)
@CompileStatic
class StorageResourceProviderServlet extends SlingSafeMethodsServlet {

    @Reference
    private CustomerRepository customerRepository
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        response.setContentType("application/json")
        final String action = request.getParameter("action") as String
        JSONObject jsonObject = new JSONObject()
        switch (action) {
            case "find":
                final String emailId = request.getParameter("email") as String
                try {
                    final CustomerEntity customerEntity = customerRepository.find(emailId)
                    response.getWriter().println(new JsonBuilder(customerEntity).toString())
                } catch (RepositoryException | Exception e) {
                    log.error("Failed to find Customer", e)
                    jsonObject.put("Error Message", "Count not able to find customer for email id: ${emailId}")
                    response.writer.write(jsonObject.toString())
                }
                break
            case "save":
                try {
                    final CustomerEntity customerEntity = new CustomerEntity()
                    customerEntity.firstName = request.getParameter("firstName") as String
                    customerEntity.lastName = request.getParameter("lastName") as String
                    customerEntity.email = request.getParameter("email") as String
                    customerEntity.password = request.getParameter("password") as String
                    customerRepository.saveOrUpdate(customerEntity);
                    response.getWriter().println(new JsonBuilder(customerEntity).toString())
                } catch (RepositoryException | Exception e) {
                    log.error("Failed to Save Customer", e)
                    jsonObject.put("Error Message", "Count not able to Save the customer")
                    response.writer.write(jsonObject.toString())
                }
                break
            case "delete":
                final String emailId = request.getParameter("email") as String
                try {
                    customerRepository.delete(emailId)
                    response.getWriter().println("<h1>Customer deleted: ${emailId}</h1>")
                } catch (RepositoryException | Exception e) {
                    log.error("Failed to Delete Customer", e)
                    jsonObject.put("Error Message", "Count not able to Delete the customer for email id: ${emailId}")
                    response.writer.write(jsonObject.toString())
                }
                break
            default:
                response.getWriter().println("<h1>Please select an action</h1>")
        }
    }
}