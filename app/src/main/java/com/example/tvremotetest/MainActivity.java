package com.example.tvremotetest;

import android.app.Activity;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
  private ConsumerIrManager ir;
  private TextView status;
  private LinearLayout root;
  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    ir=(ConsumerIrManager)getSystemService(CONSUMER_IR_SERVICE);
    ScrollView scroll=new ScrollView(this);
    root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(22,25,22,25);
    scroll.addView(root);setContentView(scroll);
    title("TV Remote · диагностика v0.3");
    status=new TextView(this);status.setTextSize(15);root.addView(status);
    status.setText(ir!=null && ir.hasIrEmitter()?"ИК-передатчик обнаружен":"ИК-передатчик не обнаружен");
    title("Samsung UE42F5020AK · проверка ИК");
    info("Сначала проверьте TV питание и громкость. Вариант A — NEC: байты по порядку, биты младшими вперёд. B — старый вариант для сравнения.");
    button("TV питание · A",()->sendSamsung(0x40, false));
    button("TV громкость + · A",()->sendSamsung(0xE0, false));
    button("TV громкость − · A",()->sendSamsung(0xD0, false));
    button("TV Mute · A",()->sendSamsung(0xF0, false));
    button("TV питание · B",()->sendSamsung(0x40, true));
    button("TV громкость + · B",()->sendSamsung(0xE0, true));
    button("TV громкость − · B",()->sendSamsung(0xD0, true));
    title("T95 W2 · экспериментальные коды");
    info("Коды из обсуждения CoreELEC не содержат полной формы ИК-сигнала. Ниже — гипотеза NEC с адресом 0x02FE. Команды могут не работать. Тестируйте, направляя ИК-порт телефона на приставку.");
    button("Вверх",()->sendT95(0x16));button("Вниз",()->sendT95(0x1A));
    button("Влево",()->sendT95(0x13));button("Вправо",()->sendT95(0x51));
    button("Домой",()->sendT95(0x11));button("Назад",()->sendT95(0x19));
    info("Если Samsung работает, а T95 нет — нужен полный файл remote.conf / ir-keytable или запись сигналов пульта. Не пытайтесь перебирать коды питания вслепую.");
  }
  private void title(String s){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.rgb(20,50,80));t.setTextSize(21);t.setPadding(0,18,0,10);root.addView(t);}
  private void info(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(14);t.setPadding(0,8,0,12);root.addView(t);}
  private void button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);root.addView(b);b.setOnClickListener(v->r.run());}
  private void sendSamsung(int cmd, boolean reverseBits){
    int code=(0xE0E0<<16) | ((cmd&255)<<8) | ((~cmd)&255);
    sendNec(code, reverseBits ? 1 : 2, "Samsung " + (reverseBits ? "B" : "A"));
  }
  private void sendT95(int cmd){ // Hypothesis only: 0x02FE + command + complement
    int code=(0x02FE<<16) | ((cmd&255)<<8) | ((~cmd)&255);sendNec(code, 2, "T95 гипотеза");
  }
  private void sendNec(int code, int mode, String label){
    if(ir==null||!ir.hasIrEmitter()){status.setText("Нет доступного ИК-передатчика");return;}
    int[] pulses=new int[67];pulses[0]=9000;pulses[1]=4500;
    for(int i=0;i<32;i++){
      int shift;
      if(mode==2) shift=8*(3-i/8)+(i%8); // NEC: bytes in written order, LSB first within each byte
      else shift=i; // Previous diagnostic variant
      pulses[2+i*2]=560;
      pulses[3+i*2]=((code >>> shift)&1)==1?1690:560;
    }
    pulses[66]=560;
    try {ir.transmit(38000,pulses);status.setText(String.format("%s: передан код %08X (38 кГц)",label,code));}
    catch(Exception e){status.setText("Ошибка ИК: "+e.getMessage());}
  }
}
