package com.example.ebike_bt;
import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class DataBt extends Activity {

    String filtered;

    //Views
    TextView tvVoltageBattery, tvPercentBattery, tvWatts, tvAmperes, tvspeed2,tvAmperesMax, tvWattsMax;
    ProgressBar batteryProgressBar;

    Chart chart;

    double voltage;
    double amperes,maxAmperes, watts,maxWatts;
    //double speed2;

    DataBt(Activity activity) {

          tvVoltageBattery =activity.findViewById(R.id.id_text_volts);
          tvAmperes=activity.findViewById(R.id.id_amperes);
          tvWatts=activity.findViewById(R.id.id_watts);
          batteryProgressBar=activity.findViewById(R.id.id_battery_bar);
          tvPercentBattery=activity.findViewById(R.id.id_volt_percent);
        //  tvspeed2=activity.findViewById(R.id.id_speed2);

        tvAmperesMax=activity.findViewById(R.id.id_max_amperes);
        tvWattsMax=activity.findViewById(R.id.id_max_watts);

        chart= new Chart( activity);
      }

      public void splitBtInfo(byte[] readBuf){

          String rawMessage = new String( readBuf, StandardCharsets.UTF_8 );

          String[] parts = rawMessage.split(";");

          int numParts= parts.length;

          for (int i = 0; i < numParts; i++) {

              if (parts[i].contains("v")){
                 String strVoltage = filter('v', parts[i]);
                  uiVoltage(strVoltage);
                  break;
              }

              else if (parts[i].contains("a")){
                String  strAmperes = filter('a', parts[i]);
                  uiAmperes(strAmperes);
                  break;
              }

              else if (parts[i].contains("s")){
                String  strSpeed = filter('s', parts[i]);
                  //uiSpeed(strSpeed);
                  break;
              }

          }

      }


    String filter(char caracter , String parte){

          int largo =parte.length();
          for (int i = 0; i < largo; i++) {
              if (caracter == parte.charAt(i)) { // si encuentra la letra v
                  try {
                       filtered = parte.substring(i + 1, largo); //coje solo los numeros, quita letra v etc
                      break;
                  }
                  catch (Exception E){}
              }
          }
          return filtered;
      }


      private void uiVoltage(String volt){

          double max = 67.2, min = 48; // maximo y minimo de bateria 16s litio

          try{
               voltage = Double.parseDouble(volt);}

          catch (Exception E){

          }

            String strVoltage = String.format(Locale.ENGLISH, "%.2f", voltage);//un decimal maximo en voltaje

            tvVoltageBattery.setText(String.format("%sV", strVoltage));// convierte a string y pone V al final}

            double batteryPercent = (voltage - min) * 100 / (max - min) + 0; // Mapea maximo y menimo  resultado 0-100% maximo


            strVoltage = String.format(Locale.ENGLISH, "%.0f%%", batteryPercent);//un decimal maximo en voltaje

            tvPercentBattery.setText(strVoltage);//pone un % al final

            batteryProgressBar.setProgress((int) batteryPercent);// muestra sin decimales

            chart.addEntry(voltage);


      }

    private void uiAmperes(String amp) {

        try{
            amperes= Double.parseDouble(amp);
        }
        catch (Exception E){
        }


        if (amperes<0){ amperes = amperes*-1;} //convierte negativo a positivo

        String strAmperes = String.format(Locale.ENGLISH, "%.2f", amperes);
        tvAmperes.setText(String.format("%sA", strAmperes));
        watts = amperes * voltage;
        String strWatts = Double.toString(amperes * voltage);
        tvWatts.setText(String.format("%sW", strWatts));

        chart.addEntry2(amperes);

        //MAX AMP
        if(amperes> maxAmperes){
            maxAmperes = amperes;
            String strMaxAmperes = Double.toString(maxAmperes);
            tvAmperesMax.setText(String.format("%sA", strMaxAmperes));

           // tvAmperesMax.setText(String.valueOf(maxAmperes));
        }

        //MAX WATT
        if(watts> maxWatts){
            maxWatts = watts;
            String strMaxWatts = String.format(Locale.ENGLISH, "%.0f", maxWatts);
            tvWattsMax.setText(String.format("%sW", strMaxWatts));
            //tvWattsMax.setText(String.valueOf(maxWatts));
        }

    }

    private void uiSpeed(String strSpeed) {

        try{
           // speed2= Double.parseDouble(strSpeed);
        }
        catch (Exception E){
         //   tvspeed2.setText(String.format("%s", strSpeed + "pp"));
        }

       // String strSpeed2 = String.format(Locale.ENGLISH, "%.0f", speed2);
        //tvspeed2.setText(String.format("%s", strSpeed2));
        //chart.addEntry3(speed2);

    }

}


