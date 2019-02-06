package tofu.mishazawa.vibrator;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
  String DEBUG_TAG = "tofu -> ";

  long lastDown = 0;
  long lastUp = 0;

  ArrayList<Long> deltas;
  Vibrator vib = null;

  Button recButton;
  ToggleButton toggleButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    deltas = new ArrayList<>();
    vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    View tapView = findViewById(R.id.tapView);

    recButton = findViewById(R.id.rec_button);
    toggleButton = findViewById(R.id.toggle_button);

    toggleButton.setOnCheckedChangeListener(this);

    recButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cancel();
        deltas.clear();
        toggleButton.setChecked(false);
      }
    });

    tapView.setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        if (toggleButton.isChecked()) return toggleButton.isChecked();

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
          lastDown = System.currentTimeMillis();
          deltas.add(lastDown - lastUp);
        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
          lastUp = System.currentTimeMillis();
          deltas.add(lastUp - lastDown);
        }

        if (deltas.size() == 22) {
          deltas.remove(0);
          toggleButton.setChecked(true);
          vibrate();
        }

        return true;
      }
    });
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) vibrate();
    else cancel();
  }


  void vibrate () {
    if (deltas.size() == 0) return;
    long[] vibes = new long[deltas.size()];
    for (int i = 0; i < deltas.size(); i++) {
      vibes[i] = deltas.get(i);
    }
    vib.vibrate(vibes, 1);
  }

  void cancel () {
    vib.cancel();
  }

}
