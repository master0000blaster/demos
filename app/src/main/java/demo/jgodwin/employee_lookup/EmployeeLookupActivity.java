package demo.jgodwin.employee_lookup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import demo.jgodwin.employee_lookup.adapters.EmployeeListAdapter;
import demo.jgodwin.employee_lookup.fragments.EditEmployeeDialog;
import demo.jgodwin.employee_lookup.models.DeleteEmployeeResponse;
import demo.jgodwin.employee_lookup.models.Employee;
import demo.jgodwin.employee_lookup.models.EmployeeRequestModel;
import demo.jgodwin.employee_lookup.services.EmployeeLookupService;

public class EmployeeLookupActivity extends AppCompatActivity {

    private EmployeeLookupService empLookupService = new EmployeeLookupService(this);
    private RecyclerView employeeRecyclerView;
    private RecyclerView.Adapter employeeAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Employee> allEmployees;
    private TextView textViewTotalEmployees;
    private TextView textViewTotalShowing;
    private EditText editTextSearchTerm;
    private ImageButton imageButtonClear;
    private ProgressBar progressBarLoading;
    private FloatingActionButton floatButtonAdd;
    private Consumer<Employee> employeeSelectedCallback;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_lookup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        employeeRecyclerView = (RecyclerView) findViewById(R.id.employeeRecyclerView);
        textViewTotalEmployees = (TextView) findViewById(R.id.textViewTotalEmployees);
        textViewTotalShowing = (TextView) findViewById(R.id.textViewTotalShowing);
        editTextSearchTerm = (EditText) findViewById(R.id.editTextSearchTerm);
        imageButtonClear = (ImageButton) findViewById(R.id.imageButtonClear);
        progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
        floatButtonAdd = (FloatingActionButton) findViewById(R.id.floatButtonAdd);
        setSupportActionBar(toolbar);

        layoutManager = new LinearLayoutManager(this);
        employeeRecyclerView.setLayoutManager(layoutManager);

        progressBarLoading.setVisibility(View.GONE);
        loadAllEmployees();

        imageButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
            }
        });

        editTextSearchTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterEmployeeList();
            }
        });

        employeeSelectedCallback = new Consumer<Employee>() {
            @Override
            public void accept(Employee employee) {
                showEditEmployeeDialog(employee);
            }
        };

        floatButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateEmployeeDialog();
            }
        });
    }

    private void updateEmployee(Employee employee) {
        EmployeeRequestModel request = new EmployeeRequestModel();
        request.age = employee.employee_age;
        request.id = employee.id;
        request.name = employee.employee_name;
        request.salary = employee.employee_salary;

        empLookupService.updateEmployeeById(employee.id, request, new Consumer<Employee>() {
            @Override
            public void accept(Employee emp) {
                Toast toasty = Toast.makeText(context, "Employee Updated!!", Toast.LENGTH_SHORT);
                toasty.setGravity(Gravity.TOP, 0, 0);
                toasty.show();

                loadAllEmployees(emp.employee_name);
            }
        });
    }

    private void createEmployee(final Employee employee) {
        EmployeeRequestModel request = new EmployeeRequestModel();
        request.age = employee.employee_age;
        request.name = employee.employee_name;
        request.salary = employee.employee_salary;

        empLookupService.createEmployee(request, new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Toast toasty = Toast.makeText(context, "Employee Created!!", Toast.LENGTH_SHORT);
                toasty.setGravity(Gravity.TOP, 0, 0);
                toasty.show();

                loadAllEmployees(employee.employee_name);
            }
        });
    }

    private void deleteEmployee(final Employee employee) {

        empLookupService.deleteEmployeeById(employee.id, new Consumer<DeleteEmployeeResponse>() {
            @Override
            public void accept(DeleteEmployeeResponse deleteEmployeeResponse) {
                Toast toasty = Toast.makeText(context, "Employee Deleted!!", Toast.LENGTH_SHORT);
                toasty.setGravity(Gravity.TOP, 0, 0);
                toasty.show();

                loadAllEmployees();
            }
        });
    }

    private void showEditEmployeeDialog(Employee employee) {
        FragmentManager fragManager = getSupportFragmentManager();
        final EditEmployeeDialog editEmployeeDialog = EditEmployeeDialog.newInstance(employee, true);

        editEmployeeDialog.setEditEmployeeCallback(new Consumer<Employee>() {
            @Override
            public void accept(Employee employee) {
                editEmployeeDialog.dismiss();
                updateEmployee(employee);
            }
        });

        editEmployeeDialog.setDeleteEmployeeCallback(new Consumer<Employee>() {
            @Override
            public void accept(Employee employee) {
                editEmployeeDialog.dismiss();
                deleteEmployee(employee);
            }
        });

        editEmployeeDialog.show(fragManager, "fragment_alert");
    }

    private void showCreateEmployeeDialog() {
        FragmentManager fragManager = getSupportFragmentManager();
        final EditEmployeeDialog createEmployeeDialog = EditEmployeeDialog.newInstance(null, false);

        createEmployeeDialog.setEditEmployeeCallback(new Consumer<Employee>() {
            @Override
            public void accept(Employee employee) {
                createEmployeeDialog.dismiss();
                createEmployee(employee);
            }
        });

        createEmployeeDialog.setDeleteEmployeeCallback(new Consumer<Employee>() {
            @Override
            public void accept(Employee employee) {
                createEmployeeDialog.dismiss();
                deleteEmployee(employee);
            }
        });

        createEmployeeDialog.show(fragManager, "fragment_alert");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_lookup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterEmployeeList() {
        String searchTerm = editTextSearchTerm.getText().toString();

        if (searchTerm == null || searchTerm.trim() == "") {
            textViewTotalShowing.setText(String.valueOf(allEmployees.size()));

            employeeAdapter = new EmployeeListAdapter(allEmployees, employeeSelectedCallback);
            employeeRecyclerView.setAdapter(employeeAdapter);
            return;
        }

        List<Employee> filteredEmployees = new ArrayList<>();

        for (Employee emp : allEmployees) {
            if (String.valueOf(emp.employee_salary).toLowerCase().contains(searchTerm.toLowerCase()) ||
                    String.valueOf(emp.employee_age).toLowerCase().contains(searchTerm.toLowerCase()) ||
                    emp.employee_name.toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredEmployees.add(emp);
            }
        }

        textViewTotalShowing.setText(String.valueOf(filteredEmployees.size()));

        employeeAdapter = new EmployeeListAdapter(filteredEmployees, employeeSelectedCallback);
        employeeRecyclerView.setAdapter(employeeAdapter);
    }

    private void clearFilter() {
        editTextSearchTerm.setText("");

        employeeAdapter = new EmployeeListAdapter(allEmployees, employeeSelectedCallback);
        employeeRecyclerView.setAdapter(employeeAdapter);
    }

    private void loadAllEmployees() {
        loadAllEmployees(null);
    }

    private void loadAllEmployees(final String postLoadSearchText) {

        progressBarLoading.setVisibility(View.VISIBLE);
        empLookupService.getAllEmployees(new Consumer<Employee[]>() {
            @Override
            public void accept(Employee[] employees) {
                progressBarLoading.setVisibility(View.GONE);
                allEmployees = Arrays.asList(employees);
                textViewTotalEmployees.setText(String.valueOf(allEmployees.size()));
                textViewTotalShowing.setText(String.valueOf(allEmployees.size()));

                employeeAdapter = new EmployeeListAdapter(allEmployees, employeeSelectedCallback);
                employeeRecyclerView.setAdapter(employeeAdapter);
                if(postLoadSearchText != null && postLoadSearchText.trim() != ""){
                    editTextSearchTerm.setText(postLoadSearchText);
                }
                else{
                    editTextSearchTerm.setText("");
                }
            }
        });
    }
}
