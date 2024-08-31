package com.example.appemprestimo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appemprestimo.util.Price;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "dados.cfg";
    private static final String DEFAULT_VALOR = "1000";
    private static final String DEFAULT_PRAZO = "12";
    private static final String DEFAULT_JUROS = "250";

    private EditText etValor;
    private SeekBar sbPrazo, sbJuros;
    private TextView tvPrazo, tvJuros, tvParcela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        configurarInterface();
        configurarEventos();
    }

    private void configurarInterface() {
        etValor = findViewById(R.id.etValor);
        sbPrazo = findViewById(R.id.sbPrazo);
        tvPrazo = findViewById(R.id.tvPrazo);
        sbJuros = findViewById(R.id.sbJuros);
        tvJuros = findViewById(R.id.tvJuros);
        tvParcela = findViewById(R.id.tvParcela);

        etValor.setText(DEFAULT_VALOR);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarEventos() {
        sbPrazo.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener(
                progress -> {
                    tvPrazo.setText(String.valueOf(progress));
                    calcularParcela();
                }
        ));

        sbJuros.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener(
                progress -> {
                    tvJuros.setText(formatarJuros(progress));
                    calcularParcela();
                }
        ));

        etValor.setOnKeyListener((view, keyCode, event) -> {
            calcularParcela();
            return false;
        });

        etValor.setOnClickListener(view -> etValor.selectAll());
    }

    @Override
    protected void onStart() {
        super.onStart();
        carregarPreferencias();
    }

    @Override
    protected void onStop() {
        super.onStop();
        salvarPreferencias();
    }

    private void carregarPreferencias() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etValor.setText(prefs.getString("valor", DEFAULT_VALOR));
        sbPrazo.setProgress(Integer.parseInt(prefs.getString("prazo", DEFAULT_PRAZO)));
        sbJuros.setProgress(Integer.parseInt(prefs.getString("juros", DEFAULT_JUROS)));
        calcularParcela();
    }

    private void salvarPreferencias() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("valor", etValor.getText().toString());
        editor.putString("juros", String.valueOf(sbJuros.getProgress()));
        editor.putString("prazo", String.valueOf(sbPrazo.getProgress()));
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itParcelas) {
            abrirPlanilhaParcelas();
        } else if (item.getItemId() == R.id.itFechar) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirPlanilhaParcelas() {
        Intent intent = new Intent(this, PlanilhaActivity.class);
        intent.putExtra("valor", etValor.getText().toString());
        intent.putExtra("taxa", tvJuros.getText().toString());
        intent.putExtra("meses", sbPrazo.getProgress());
        startActivity(intent);
    }

    private void calcularParcela() {
        if (!etValor.getText().toString().isEmpty()) {
            double valor = Double.parseDouble(etValor.getText().toString());
            double juros = sbJuros.getProgress() / 100.0;
            int prazo = sbPrazo.getProgress();
            double parcela = Price.calcParcela(valor, juros, prazo);
            tvParcela.setText(String.format(Locale.US, "%.2f", parcela));
        }
    }

    private String formatarJuros(int progress) {
        return String.format(Locale.US, "%.2f", progress / 100.0);
    }

    // Classe interna para simplificar os eventos de SeekBar
    private static class SimpleSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        private final OnProgressChangedListener listener;

        public SimpleSeekBarChangeListener(OnProgressChangedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            listener.onProgressChanged(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    }

    // Interface funcional para o listener da SeekBar
    @FunctionalInterface
    private interface OnProgressChangedListener {
        void onProgressChanged(int progress);
    }
}
