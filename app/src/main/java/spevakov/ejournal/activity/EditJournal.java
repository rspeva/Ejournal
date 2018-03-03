package spevakov.ejournal.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;

public class EditJournal extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    String groupEng, titleEng, group, title;
    String[] theme, dates, surnameEngArr, surnameArr, types, markList, dz;
    LinearLayout llSurname, llJournal;
    DBHelper dbHelper;
    int var, n;
    boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_marks_group);
        progressBar = (ProgressBar) findViewById(R.id.progressBarSubjects);

        progressBar.setVisibility(ProgressBar.VISIBLE);

        Intent intent = getIntent();
        title = intent.getExtras().getString("Title");
        group = intent.getExtras().getString("Groups");
        groupEng = intent.getExtras().getString("GroupEng");
        titleEng = intent.getExtras().getString("TitleEng");
        surnameEngArr = intent.getExtras().getStringArray("SurnameEngArr");
        surnameArr = intent.getExtras().getStringArray("SurnameArr");
        TextView tvTitle = (TextView) findViewById(R.id.tvTitleJournal);
        tvTitle.setText(group + " | " + title);

        llSurname = (LinearLayout) findViewById(R.id.llSurname);

        dbHelper = new DBHelper(this);
        dbHelper.openDataBase();


        if (dbHelper.tableExists(groupEng, titleEng)) {
            Cursor cursor = dbHelper.query(groupEng + "_" + titleEng, null, null, null, "Date");
            if (cursor.moveToFirst()) {
                dates = new String[cursor.getCount()];
                theme = new String[cursor.getCount()];
                types = new String[cursor.getCount()];
                dz = new String[cursor.getCount()];
                markList = new String[cursor.getCount() * surnameArr.length];
                int i = 0, k = 0;
                do {
                    dates[i] = cursor.getString(0);
                    theme[i] = cursor.getString(1);
                    dz[i] = cursor.getString(2);
                    types[i] = cursor.getString(3);
                    for (int j = 0; j < surnameEngArr.length; j++) {
                        markList[k] = cursor.getString(j + 4);
                        k++;
                    }
                    i++;
                } while (cursor.moveToNext());
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lButtonParams.setMargins(0, 3, 0, 3);

                for (i = -1; i < surnameArr.length; i++) {
                    Button btn = new Button(getApplicationContext());
                    btn.setLayoutParams(lButtonParams);

                    if (i == -1) {
                        ImageView iv = new ImageView(getApplicationContext());
                        iv.setImageResource(R.drawable.iv_back);
                        iv.setPadding(0, 0, 0, 6);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog(2);
                            }
                        });
                        llSurname.addView(iv);
                    } else {
                        btn.setText(surnameArr[i]);
                        btn.setTextColor(Color.BLACK);
                        btn.setClickable(true);
                        btn.setBackgroundColor(getResources().getColor(R.color.colorSurname));
                        llSurname.addView(btn);
                    }
                }
                createLayout(ListMarksGroup.checked);
            } else showDialog(1);
        } else showDialog(1);
    }

    @Override
    public void onBackPressed() {
        showDialog(2);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Нет проведенных пар!");
            adb.setIcon(R.drawable.ic_layers_clear_black_24dp);
            adb.setPositiveButton("Закрыть", myClickListener);
            return adb.create();
        } else if (id == 2) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Сохранить изменения?");
            adb.setIcon(R.drawable.ic_save_black_24dp);
            adb.setPositiveButton("Да", mClickListener);
            adb.setNegativeButton("Нет", mClickListener);
            adb.setNeutralButton("Отмена", mClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    finish();
                    break;

            }
        }
    };

    DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
//                    HashMap subject;
//                    int x = 0;
//                    status = true;
//                    progressBar.setVisibility(ProgressBar.VISIBLE);
//                    for (int k = 0; k < dates.length; k++) {
//                        if (dates[k].equals("0000")) {
//                           dbHelper.openDataBase();
//                           dbHelper.deleteLesson(groupEng, titleEng, dates[k], types[k]);
//                        } else {
//
//                            subject = new HashMap<>();
//                            subject.put("objectId", objectid[k]);
//                            subject.put("DZ", dz[k]);
//                            subject.put("Theme", theme[k]);
//                            subject.put("Type", types[k]);
//                            subject.put("Dates", dates[k]);
//                            for (int i = 0; i < surnameEngArr.length; i++) {
//                                subject.put(surnameEngArr[i], markList[i + x]);
//                            }
//                            Backendless.Data.of(groupEng + "_" + titleEng).save(subject, new AsyncCallback<Map>() {
//                                @Override
//                                public void handleResponse(Map response) {
//                                    status = true;
//                                }
//
//                                @Override
//                                public void handleFault(BackendlessFault fault) {
//                                    status = false;
//                                }
//                            });
//
//                            x += surnameEngArr.length;
//                        }
                  //  }

                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (status) finish();
                    else
                        Toast.makeText(getApplicationContext(), "Нет подключения к БД", Toast.LENGTH_SHORT).show();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    finish();
                    break;
                case Dialog.BUTTON_NEUTRAL:
                    break;


            }
        }
    };

    void createLayout(boolean check) {
        llJournal = (LinearLayout) findViewById(R.id.llJournal);
        llJournal.removeAllViewsInLayout();
        LinearLayout.LayoutParams llVerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lButtonParams.setMargins(0, 3, 0, 3);
        var = 0;
        for (int i = 0; i < dates.length; i++) {
            LinearLayout llVertical = new LinearLayout(this);
            llVertical.setLayoutParams(llVerParams);
            llVertical.setOrientation(LinearLayout.VERTICAL);
            llVertical.setPadding(0, 0, 5, 0);

            Button btn1 = new Button(this);
            btn1.setLayoutParams(lButtonParams);
            btn1.setText(dates[i]);
            btn1.setBackgroundResource(R.drawable.btn_date_journal);
            btn1.setClickable(true);
            btn1.setOnClickListener(this);
            btn1.setId(i);
            llVertical.addView(btn1);

            for (int j = 0; j < surnameArr.length; j++) {
                final Button btn = new Button(this);
                btn.setLayoutParams(lButtonParams);
                if (markList[var].equals("0"))
                    btn.setText(" ");
                else
                    btn.setText(String.valueOf(markList[var]));
                btn.setId(var);
                var++;
                btn.setClickable(false);
                if (check) {
                    btn.setBackgroundColor(btnColor(i));
                } else btn.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

                btn.setGravity(Gravity.CENTER);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorDrawable buttonColor = (ColorDrawable) ((Button) v).getBackground();
                        int colorId = buttonColor.getColor();
                        ((Button) v).setBackgroundColor(getResources().getColor(R.color.colorButtonActive));
                        showPopupMenu(v, colorId);
                    }
                });

                llVertical.addView(btn);
            }
            llJournal.addView(llVertical);

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), SubjectInfo.class);
        intent.putExtra("Date", dates[v.getId()]);
        intent.putExtra("Theme", theme[v.getId()]);
        intent.putExtra("Type", types[v.getId()]);
        intent.putExtra("DZ", dz[v.getId()]);
        intent.putExtra("id", v.getId());
        intent.putExtra("checked", ListMarksGroup.checked);
        intent.putExtra("edit", true);
        intent.putExtra("GroupEng", groupEng);
        intent.putExtra("TitleEng", titleEng);


        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ListMarksGroup.checked = data.getExtras().getBoolean("checked");
            if (data.getExtras().getBoolean("changed")) {
                dates[data.getExtras().getInt("id")] = data.getExtras().getString("Dates");
                dz[data.getExtras().getInt("id")] = data.getExtras().getString("DZ");
                theme[data.getExtras().getInt("id")] = data.getExtras().getString("Theme");
                types[data.getExtras().getInt("id")] = data.getExtras().getString("Type");
            }
            createLayout(ListMarksGroup.checked);
        }

    }

    private int btnColor(int id) {
        switch (types[id]) {
            case "Практическая":
                return getResources().getColor(R.color.colorPR);
            case "Лабороторная":
                return getResources().getColor(R.color.colorLR);
            case "Контрольная":
                return getResources().getColor(R.color.colorKR);
            case "Атестация":
                return getResources().getColor(R.color.colorAT);
            case "Семестровая":
                return getResources().getColor(R.color.colorSM);
            default:
                return getResources().getColor(R.color.colorNeutral);
        }
    }


    private void showPopupMenu(final View v, final int colorid) {
        n = v.getId();
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
        popupMenu.inflate(R.menu.popupmenu);
        if (((Button) v).getText() != "" && ((Button) v).getText() != " ")
            popupMenu.getMenu().findItem(R.id.menuClear).setEnabled(true);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((Button) v).setBackgroundColor(colorid);
                switch (item.getItemId()) {
                    case R.id.menuN:
                        ((Button) v).setText("H");
                        markList[n] = "Н";
                        return true;
                    case R.id.menu2:
                        ((Button) v).setText("2");
                        markList[n] = "2";
                        return true;
                    case R.id.menu3:
                        ((Button) v).setText("3");
                        markList[n] = "3";
                        return true;
                    case R.id.menu4:
                        ((Button) v).setText("4");
                        markList[n] = "4";
                        return true;
                    case R.id.menu5:
                        ((Button) v).setText("5");
                        markList[n] = "5";
                        return true;
                    case R.id.menuClear:
                        ((Button) v).setText("");
                        markList[n] = "0";
                        item.setEnabled(false);
                        return true;
                    default:
                        ((Button) v).setBackgroundColor(colorid);
                        return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                ((Button) v).setBackgroundColor(colorid);
            }
        });
        popupMenu.show();
    }


}
