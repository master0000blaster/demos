package demo.jgodwin.employee_lookup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Consumer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import demo.jgodwin.employee_lookup.R;
import demo.jgodwin.employee_lookup.models.Employee;

public class EditEmployeeDialog extends DialogFragment {

    private Employee employee;

    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextSalary;

    private Button buttonSave;
    private Button buttonDelete;
    private Button buttonCancel;

    private TextView textViewEmployeeId;
    private TextView textViewIdLabel;

    private Consumer<Employee> saveEmployeeCallBack;
    private Consumer<Employee> deleteEmployeeCallBack;

    public EditEmployeeDialog() {
    }

    public static EditEmployeeDialog newInstance(Employee employee, boolean isEditMode) {
        EditEmployeeDialog frag = new EditEmployeeDialog();
        Bundle args = new Bundle();

        if(employee == null && !isEditMode)
        {
            employee = new Employee();
        }

        args.putSerializable("employee", employee);
        args.putBoolean("isEditMode", isEditMode);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_employee_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextName = (EditText) view.findViewById(R.id.editTextName);
        editTextAge = (EditText) view.findViewById(R.id.editTextAge);
        editTextSalary = (EditText) view.findViewById(R.id.editTextSalary);
        buttonSave = (Button) view.findViewById(R.id.buttonSave);
        buttonDelete = (Button) view.findViewById(R.id.buttonDelete);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
        textViewEmployeeId = (TextView) view.findViewById(R.id.textViewEmployeeId);
        textViewIdLabel = (TextView) view.findViewById(R.id.textViewIdLabel);

        employee = (Employee) getArguments().getSerializable("employee");
        boolean isEditMode = getArguments().getBoolean("isEditMode");

        if(isEditMode) {
            getDialog().setTitle("Edit Employee");
            buttonDelete.setVisibility(View.VISIBLE);
            textViewEmployeeId.setVisibility(View.VISIBLE);
            textViewIdLabel.setVisibility(View.VISIBLE);
            textViewEmployeeId.setText(String.valueOf(employee.id));
        }
        else{ // is create mode
            getDialog().setTitle("Create Employee");
            buttonDelete.setVisibility(View.INVISIBLE);
            textViewEmployeeId.setVisibility(View.INVISIBLE);
            textViewIdLabel.setVisibility(View.INVISIBLE);
        }

        editTextName.setText(employee.employee_name);
        editTextAge.setText(String.valueOf(employee.employee_age));
        editTextSalary.setText(String.valueOf(employee.employee_salary));


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteEmployeeCallBack != null){
                    deleteEmployeeCallBack.accept(employee);
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmployee();
            }
        });
    }

    private void close(){
        super.dismiss();
    }

    private void saveEmployee() {
        if(saveEmployeeCallBack == null){
            return;
        }

        float salary = 0;
        int age = 0;
        try {
            age = Integer.parseInt(editTextAge.getText().toString());
        }
        catch (Exception e){
            showToastMessage("Please enter a whole number for Age.");
            return;
        }

        try {
            salary = Float.parseFloat(editTextSalary.getText().toString());
        }
        catch (Exception e){
            showToastMessage("Please enter a decimal number for Salary.");
            return;
        }

        employee.employee_salary = salary;
        employee.employee_name = editTextName.getText().toString().trim();
        employee.employee_age = age;

        saveEmployeeCallBack.accept(employee);
    }

    private void showToastMessage(String message){
        Toast toasty = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toasty.setGravity(Gravity.TOP, 0, 0);
        toasty.show();
    }

    public void setDeleteEmployeeCallback(Consumer<Employee> deleteEmployeeCallBack ){

        this.deleteEmployeeCallBack = deleteEmployeeCallBack;
    }

    public void setEditEmployeeCallback(Consumer<Employee> addEditCallback ){
        saveEmployeeCallBack = addEditCallback;
    }

}
