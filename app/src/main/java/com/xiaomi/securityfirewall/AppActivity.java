package com.xiaomi.securityfirewall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomi.securityfirewall.Models.AppListMain;
import com.xiaomi.securityfirewall.Models.CustomAppListAdapter;
import com.xiaomi.securityfirewall.Models.GridSpacingItemDecoration;
import com.xiaomi.securityfirewall.Models.RecyclerItemClickListener;
import com.xiaomi.securityfirewall.Models.User;
import com.xiaomi.securityfirewall.Services.DataContext;
import com.xiaomi.securityfirewall.Services.LocalUserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppActivity extends AppCompatActivity {
    private PackageManager packageManager;
    private ArrayList<AppListMain> appListMainArrayList;
    private RecyclerView rvAppList;
    private CustomAppListAdapter customAppListAdapter;
    private AppListMain appListMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        loadApps();
        loadListView();
    }
    public void loadApps() {
        try {
            packageManager = getPackageManager();
            appListMainArrayList = new ArrayList<>();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfoList) {
                AppListMain appListMain = new AppListMain();
                appListMain.setAppIcon(resolveInfo.activityInfo.loadIcon(packageManager));
                appListMain.setAppName(resolveInfo.loadLabel(packageManager).toString());
                appListMain.setAppPackage(resolveInfo.activityInfo.packageName);
                appListMainArrayList.add(appListMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadListView() {
        int mColumnCount = 3;
        rvAppList = findViewById(R.id.rvAppList);
        rvAppList.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        rvAppList.addItemDecoration(new GridSpacingItemDecoration(10)); // 16px. In practice, you'll want to use getDimensionPixelSize

        Collections.sort(appListMainArrayList, new Comparator<AppListMain>() {
            @Override
            public int compare(AppListMain lhs, AppListMain rhs) {
                return lhs.getAppName().toString().compareTo(rhs.getAppName().toString());
            }

        });
        customAppListAdapter = new CustomAppListAdapter(this, appListMainArrayList);
        rvAppList.setAdapter(customAppListAdapter);

        rvAppList.addOnItemTouchListener(new RecyclerItemClickListener(this, rvAppList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                appListMain = appListMainArrayList.get(position);
                if (appListMain != null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            AppActivity.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Bloquear tráfico");

                    // Setting Dialog Message
                    alertDialog.setMessage("¿Quiere bloquear el tráfico a "+appListMain.getAppName()+"?");

                    // Setting Icon to Dialog
                    //alertDialog.setIcon(hani.momanii.supernova_emoji_library.R.drawable.emoji_1f6d);
                    // Setting OK Button
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed

                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Bloquear", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            Toast.makeText(getApplicationContext(), appListMain.getAppName()+" bloqueada", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
           /* public void onItemLongClick(View view, int position) {
                appListMain = appListMainArrayList.get(position);
                if (appListMain != null) {
                    selectedPos = position;
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + appListMain.getAppPackage()));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, REQUEST_UNINSTALL);

                }
            }*/
        }));
    }
}
