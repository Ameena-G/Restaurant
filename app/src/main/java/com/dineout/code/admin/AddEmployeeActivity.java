package com.dineout.code.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dineout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddEmployeeActivity extends AppCompatActivity {

    EditText name, email, password, specialty, salary;
    Spinner type;
    TextView special;
    Button addButton;
    Long it;
    static ArrayList<Long> eid = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    boolean check = false;
    boolean check1 = true;
    ArrayList<Employee> E = new ArrayList<>();
    String e;
    String p;

    private ListView listView;
    DatabaseReference mDatabase;
    IngredientsListAdapter ingredientsListAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<IngredientRow> ingredients = new ArrayList<>();
    private ArrayList<String> notifications1 = new ArrayList<>();
    private ArrayList<String> notifications2 = new ArrayList<>();

    int idz = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_add_employee);

        name = findViewById(R.id.AddEmployeeName300);
        type = findViewById(R.id.DropDownMenu300);
        email = findViewById(R.id.AddEmployeeEmail300);
        password = findViewById(R.id.AddEmployeePassword300);
        specialty = findViewById(R.id.AddSpeciality300);
        salary = findViewById(R.id.AddEmployeeSalary300);
        special = findViewById(R.id.specialityLabel300);
        addButton = findViewById(R.id.AddNewEmployeeButton300);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Chef") || selectedItem.equals("Head Chef")) {
                    specialty.setEnabled(true);
                    specialty.setInputType(InputType.TYPE_CLASS_TEXT);
                    specialty.setFocusable(true);
                    specialty.setFocusableInTouchMode(true);
                    special.setEnabled(true);
                } else {
                    specialty.setEnabled(false);
                    specialty.setInputType(InputType.TYPE_NULL);
                    specialty.setFocusable(false);
                    special.setEnabled(false);
                }

                if (selectedItem.equals("Hall Manager") || selectedItem.equals("Head Chef")) {
                    email.setEnabled(true);
                    email.setInputType(InputType.TYPE_CLASS_TEXT);
                    email.setFocusable(true);
                    email.setFocusableInTouchMode(true);

                    password.setEnabled(true);
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
                    check = true;
                } else {
                    email.setEnabled(false);
                    email.setInputType(InputType.TYPE_NULL);
                    email.setFocusable(false);

                    password.setEnabled(false);
                    password.setInputType(InputType.TYPE_NULL);
                    password.setFocusable(false);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) { }
        });

        addButton.setOnClickListener(v -> {
            String specialtyText;
            boolean go = true;

            if (!type.getSelectedItem().toString().equals("Chef")) {
                specialtyText = "None";
            } else {
                specialtyText = type.getSelectedItem().toString();
            }

            if (name.getText().toString().isEmpty()) {
                name.setError("Name is Required");
                go = false;
            }

            if (email.getText().toString().isEmpty() &&
                    (type.getSelectedItem().toString().equals("Head Chef") ||
                            type.getSelectedItem().toString().equals("Hall Manager"))) {
                email.setError("Email Address is Required");
                go = false;
            }

            if (password.getText().toString().isEmpty() &&
                    (type.getSelectedItem().toString().equals("Head Chef") ||
                            type.getSelectedItem().toString().equals("Hall Manager"))) {
                password.setError("Password is Required");
                go = false;
            }

            if (!salary.getText().toString().isEmpty() &&
                    Integer.parseInt(salary.getText().toString()) < 1) {
                salary.setError("Salary must be greater than 0");
                go = false;
            }

            if (specialty.getText().toString().isEmpty() &&
                    type.getSelectedItem().toString().equals("Chef")) {
                specialty.setError("Specialty is Required");
                go = false;
            }

            if (go) {
                e = email.getText().toString();
                p = password.getText().toString();

                if (check) {
                    firebaseAuth.createUserWithEmailAndPassword(e, p)
                            .addOnCompleteListener(AddEmployeeActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddEmployeeActivity.this, "User is Registered", Toast.LENGTH_SHORT).show();
                                    } else {
                                        check1 = false;
                                        Toast.makeText(AddEmployeeActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                if (check1) {
                    DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference();
                    mDatabase1.child("Ids").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                            if (dataSnapshot != null) {
                                eid.add(dataSnapshot.getValue(Long.class));
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) { }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            if (E.size() == 0) {
                                ref.child("Ids").child("Employeeid").setValue(1);
                            }

                            if (!eid.isEmpty()) {
                                Employee e1 = new Employee(
                                        String.valueOf(eid.get(0)),
                                        name.getText().toString(),
                                        e,
                                        p,
                                        specialty.getText().toString(),
                                        salary.getText().toString(),
                                        type.getSelectedItem().toString()
                                );

                                ref.child("Employee").child(String.valueOf(eid.get(0))).setValue(e1);
                                ref.child("Ids").child("Employeeid").setValue(eid.get(0) + 1);
                                Toast.makeText(AddEmployeeActivity.this, "Employee Added Successfully", Toast.LENGTH_SHORT).show();
                                eid.clear();
                                startActivity(new Intent(AddEmployeeActivity.this, AdminPanelActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                    mDatabase.child("Employee").addChildEventListener(new ChildEventListener() {
                        public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                            Employee item = dataSnapshot.getValue(Employee.class);
                            E.add(item);
                        }

                        public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                        public void onChildRemoved(DataSnapshot dataSnapshot) { }

                        public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }
        });
    }
}
