package demo.jgodwin.employee_lookup.services;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.Consumer;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.*;

import org.json.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.*;
import demo.jgodwin.employee_lookup.models.DeleteEmployeeResponse;
import demo.jgodwin.employee_lookup.models.Employee;
import demo.jgodwin.employee_lookup.models.EmployeeRequestModel;

public class EmployeeLookupService {

    private static final Uri BASE_URI = Uri.parse("http://dummy.restapiexample.com/api/v1/"); // todo: maybe put the url in a config file

    private AsyncHttpClient asyncClient = new AsyncHttpClient();
    private Gson gson = new Gson();
    private Context context;

    public EmployeeLookupService(Context context) {
        this.context = context;
    }

    public void getAllEmployees(final Consumer<Employee[]> callBack) {

        asyncClient.get(getCombinedUrl("employees"), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Employee[] allEmployees = gson.fromJson(response.toString(), Employee[].class);
                        callBack.accept(allEmployees);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(
                                context,
                                "There was an error loading all Employees. " + responseString,
                                Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    public void getEmployeeById(int employeeId, final Consumer<Employee> callBack) {

        RequestParams params = new RequestParams();
        params.put("id", employeeId);

        asyncClient.get(getCombinedUrl("employee"), params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Employee employee = gson.fromJson(response.toString(), Employee.class);
                        callBack.accept(employee);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(
                                context,
                                "There was an error loading Employee. " + responseString,
                                Toast.LENGTH_LONG).show();

                    }

                }
        );
    }

    public void createEmployee(EmployeeRequestModel employee, final Consumer<Integer> callBack) {

        StringEntity jsonEntity = new StringEntity(gson.toJson(employee), "UTF-8");

        asyncClient.post(context, getCombinedUrl("create"), jsonEntity, "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        EmployeeRequestModel employeeRequestModel = gson.fromJson(response.toString(), EmployeeRequestModel.class);
                        callBack.accept(employeeRequestModel.id);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(
                                context,
                                "There was an error creating Employee. " + responseString,
                                Toast.LENGTH_LONG).show();

                    }

                }
        );
    }

    public void updateEmployeeById(final int employeeId, final EmployeeRequestModel employee, final Consumer<Employee> callBack) {

        StringEntity jsonEntity = new StringEntity(gson.toJson(employee), "UTF-8");

        String pathSegment = "update/" + employeeId;
        asyncClient.put(context, getCombinedUrl(pathSegment), jsonEntity, "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        Employee emp = new Employee();
                        emp.id = employeeId;
                        emp.employee_age = employee.age;
                        emp.employee_name = employee.name;
                        emp.employee_salary = employee.salary;

                        callBack.accept(emp);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(
                                context,
                                "There was an error while updating Employee. " + responseString,
                                Toast.LENGTH_LONG).show();

                    }

                }
        );
    }

    public void deleteEmployeeById(int employeeId, final Consumer<DeleteEmployeeResponse> callBack) {

        RequestParams params = new RequestParams();

        asyncClient.delete(getCombinedUrl("delete/" + employeeId), params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        DeleteEmployeeResponse deleteResponse =
                                gson.fromJson(response.toString(), DeleteEmployeeResponse.class);
                        callBack.accept(deleteResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(
                                context,
                                "There was an error deleting Employee. " + responseString,
                                Toast.LENGTH_LONG).show();

                    }

                }
        );
    }

    private String getCombinedUrl(String pathSegment) {
        return Uri.withAppendedPath(BASE_URI, pathSegment).toString();
    }
}