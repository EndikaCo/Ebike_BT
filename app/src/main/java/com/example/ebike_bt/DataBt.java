package com.example.ebike_bt;
import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class DataBt extends Activity {

    String filtered;

    //Views
    TextView tvVoltageBattery, tvPercentBattery, tvWatts, tvAmperes;
    ProgressBar batteryProgressBar;

    Chart chart;

    double voltage;
    double amperes;


    DataBt(Activity activity) {

          tvVoltageBattery =activity.findViewById(R.id.id_text_volts);
          tvAmperes=activity.findViewById(R.id.id_amperes);
          tvWatts=activity.findViewById(R.id.id_watts);
          batteryProgressBar=activity.findViewById(R.id.id_battery_bar);
          tvPercentBattery=activity.findViewById(R.id.id_volt_percent);

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
          catch (Exception E){}



            String strVoltage = String.format(Locale.ENGLISH, "%.2f", voltage);//un decimal maximo en voltaje

            tvVoltageBattery.setText(String.format("%sV", strVoltage));// convierte a string y pone V al final}

            double batteryPercent = (voltage - min) * 100 / (max - min) + 0; // Mapea maximo y menimo  resultado 0-100% maximo


            strVoltage = String.format(Locale.ENGLISH, "%.0f%%", batteryPercent);//un decimal maximo en voltaje

            tvPercentBattery.setText(strVoltage);//pone un % al final

            batteryProgressBar.setProgress((int) batteryPercent);// muestra sin decimales

            chart.addEntry(batteryPercent);


      }

    private void uiAmperes(String amp) {

        try{

        amperes= Double.parseDouble(amp);}

        catch (Exception E){}

            String strAmperes = String.format(Locale.ENGLISH, "%.2f", amperes);
            tvAmperes.setText(String.format("%sA", strAmperes));

             String strWatts = Double.toString(amperes * voltage);
            tvWatts.setText(String.format("%sW", strWatts));

    }




    }


