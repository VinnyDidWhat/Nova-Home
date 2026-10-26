package com.novahome.tv;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, apps;
    TextView clock;
    final int WHITE=Color.WHITE, MUTED=Color.rgb(170,185,211);
    GradientDrawable bg(int color,int radius) { GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d; }
    TextView text(String s,int size,int color) { TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); return t; }
    @Override public void onCreate(Bundle state){super.onCreate(state); render();}
    void render(){
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff142C49,0xff0B1528,0xff080B15}));
        root=new LinearLayout(this);root.setOrientation(1);root.setPadding(56,35,56,35);scroll.addView(root);setContentView(scroll);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);root.addView(header);
        TextView logo=text("NOVA  /  HOME",25,WHITE);logo.setTypeface(null,Typeface.BOLD);header.addView(logo,new LinearLayout.LayoutParams(0,-2,1));
        clock=text("",19,MUTED);header.addView(clock);updateClock();
        TextView title=text("Your entertainment. Your space.",34,WHITE);title.setTypeface(null,Typeface.BOLD);LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.topMargin=56;root.addView(title,tp);
        TextView subtitle=text("A quieter way to watch.",17,MUTED);root.addView(subtitle);
        TextView section=text("YOUR APPS",18,WHITE);section.setTypeface(null,Typeface.BOLD);LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.topMargin=46;sp.bottomMargin=20;root.addView(section,sp);
        apps=new LinearLayout(this);apps.setOrientation(LinearLayout.VERTICAL);root.addView(apps);loadApps();
        Button settings=new Button(this);settings.setText("TV SETTINGS");settings.setTextColor(WHITE);settings.setBackground(bg(0xff29405D,18));settings.setOnClickListener(v->{try{startActivity(new Intent("android.settings.SETTINGS"));}catch(Exception e){Toast.makeText(this,"Settings unavailable",Toast.LENGTH_SHORT).show();}});
        LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(240,65);bp.topMargin=34;root.addView(settings,bp);
    }
    void updateClock(){clock.setText(new SimpleDateFormat("EEE, MMM d   •   h:mm a",Locale.getDefault()).format(new Date()));clock.postDelayed(()->{if(!isFinishing())updateClock();},30000);}
    void loadApps(){
        PackageManager pm=getPackageManager();Intent query=new Intent(Intent.ACTION_MAIN);query.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> found=pm.queryIntentActivities(query,0);Collections.sort(found,new ResolveInfo.DisplayNameComparator(pm));
        LinearLayout row=null;int index=0;
        for(ResolveInfo info:found){if(info.activityInfo.packageName.equals(getPackageName()))continue;
            if(index%5==0){row=new LinearLayout(this);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.bottomMargin=14;apps.addView(row,rp);}
            final ComponentName component=new ComponentName(info.activityInfo.packageName,info.activityInfo.name);
            LinearLayout tile=new LinearLayout(this);tile.setOrientation(1);tile.setGravity(Gravity.CENTER);tile.setFocusable(true);tile.setPadding(12,12,12,12);tile.setBackground(bg(0xff263C58,18));
            ImageView icon=new ImageView(this);try{Drawable d=info.loadIcon(pm);icon.setImageDrawable(d);}catch(Exception ignored){}tile.addView(icon,new LinearLayout.LayoutParams(62,62));
            TextView label=text(info.loadLabel(pm).toString(),14,WHITE);label.setGravity(Gravity.CENTER);label.setMaxLines(2);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,55);lp.topMargin=12;tile.addView(label,lp);
            tile.setOnFocusChangeListener((v,focus)->{tile.setBackground(bg(focus?0xff3D78B5:0xff263C58,18));tile.setScaleX(focus?1.05f:1f);tile.setScaleY(focus?1.05f:1f);});
            tile.setOnClickListener(v->{try{Intent launch=new Intent(Intent.ACTION_MAIN);launch.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);launch.setComponent(component);launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(launch);}catch(Exception e){Toast.makeText(this,"Unable to open app",Toast.LENGTH_SHORT).show();}});
            LinearLayout.LayoutParams tileParams=new LinearLayout.LayoutParams(0,162,1);tileParams.rightMargin=14;row.addView(tile,tileParams);index++;
        }
        if(index==0){TextView empty=text("No TV apps found yet. Install streaming apps from Google Play.",17,MUTED);apps.addView(empty);}
    }
    @Override protected void onResume(){super.onResume();if(apps!=null){apps.removeAllViews();loadApps();}}
}
