package com.genymobile.sommeil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.widget.Toast;

public class AlarmBootReceiver extends BroadcastReceiver{
	Alarm alarm;
	Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		charger();
		planifierAlarm();
	}

    /*
     * Chargement des informations du reveil.
     * Ici pour la sauvegarde on a simplement déserialiser l'objet Alarm.
     */
    public void charger(){
    	alarm = null;
    	try {
    		ObjectInputStream alarmOIS= new ObjectInputStream(context.openFileInput("alarm.serial"));
    		try {	
				alarm = (Alarm) alarmOIS.readObject(); 
			} finally {
				try {
					alarmOIS.close();
				} finally {
					;
				}
			}
		}
    	catch(FileNotFoundException fnfe){
    		alarm = new Alarm();
        	alarm.setActive(true);
        	Time t = new Time();
        	t.hour = 7;
        	t.minute = 30;
        	alarm.setHeure(t);
    	}
    	catch(IOException ioe) {
			ioe.printStackTrace();
		}
    	catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
    }   
    
    /*
    
	
	/*
	 * Planifier l'alarm
	 */
    private void planifierAlarm() {
		//Récupération de l'instance du service AlarmManager.
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		//On instancie l'Intent qui va être appelé au moment du reveil.
		Intent intent = new Intent(context, AlarmReceiver.class);
		
		//On créer le pending Intent qui identifie l'Intent de reveil avec un ID et un/des flag(s)
		PendingIntent pendingintent = PendingIntent.getBroadcast(context, ReveilActivity.ALARM_ID, intent, 0);
		
		//On annule l'alarm pour replanifier si besoin
		am.cancel(pendingintent);
		
		//La on va déclencher un calcul pour connaitre le temps qui nous sépare du prochain reveil.
		Calendar reveil  = Calendar.getInstance();
		reveil.set(Calendar.HOUR_OF_DAY, alarm.getHeure().hour);
		reveil.set(Calendar.MINUTE, alarm.getHeure().minute);
		if(reveil.compareTo(Calendar.getInstance()) == -1)
			reveil.add(Calendar.DAY_OF_YEAR, 1);
		
		Calendar cal = Calendar.getInstance();
		reveil.set(Calendar.SECOND, 0);
		cal.set(Calendar.SECOND, 0);
		long diff = reveil.getTimeInMillis() - cal.getTimeInMillis();
		
		//On ajoute le reveil au service de l'AlarmManager
		am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis() + diff, pendingintent);
		Toast.makeText(context, "Alarme programm&eacute; le " + 
				reveil.get(Calendar.DAY_OF_MONTH) + " à " + 
				reveil.get(Calendar.HOUR_OF_DAY) + ":" + + 
				reveil.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
	}
}
