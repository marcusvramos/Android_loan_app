package com.example.appemprestimo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appemprestimo.entidades.Parcela;
import com.example.appemprestimo.util.Price;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlanilhaActivity extends AppCompatActivity {

    private ImageButton btnVoltar;
    private TextView txtValor, txtTaxa;
    private ListView listViewParcelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planilha);

        configurarInterface();
        configurarListeners();
        carregarDados();
    }

    private void configurarInterface() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVoltar = findViewById(R.id.btVoltar);
        txtValor = findViewById(R.id.tvValor);
        txtTaxa = findViewById(R.id.tvTaxa);
        listViewParcelas = findViewById(R.id.listView);
    }

    private void configurarListeners() {
        btnVoltar.setOnClickListener(view -> finish());

        listViewParcelas.setOnItemClickListener((adapterView, view, position, id) -> {
            Parcela parcela = (Parcela) adapterView.getItemAtPosition(position);
            mostrarSaldoDevedor(parcela);
        });
    }

    private void carregarDados() {
        Intent intent = getIntent();

        double valor = obterDoubleDoIntent(intent, "valor");
        double taxa = obterDoubleDoIntent(intent, "taxa");
        int meses = intent.getIntExtra("meses", 1);

        txtValor.setText(formatarValor(valor));
        txtTaxa.setText(formatarValor(taxa));

        List<Parcela> listaParcelas = gerarPlanilha(valor, taxa, meses);
        configurarListaParcelas(listaParcelas);
    }

    private double obterDoubleDoIntent(Intent intent, String chave) {
        String valorString = intent.getStringExtra(chave);
        return valorString != null ? Double.parseDouble(valorString) : 0.0;
    }

    private String formatarValor(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private void configurarListaParcelas(List<Parcela> listaParcelas) {
        View headerView = getLayoutInflater().inflate(R.layout.header_listview, null);
        listViewParcelas.addHeaderView(headerView);

        EmprestimoAdapter adapter = new EmprestimoAdapter(this, R.layout.item_listview, listaParcelas);
        listViewParcelas.setAdapter(adapter);
    }

    private void mostrarSaldoDevedor(Parcela parcela) {
        String mensagem = "Falta ainda R$ " + parcela.getSdDevedor() + " para quitar o empréstimo";
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    private List<Parcela> gerarPlanilha(double valorInicial, double taxa, int meses) {
        List<Parcela> listaParcelas = new ArrayList<>();
        double valorAtual = valorInicial;
        double valorParcela = Price.calcParcela(valorInicial, taxa, meses);

        for (int numeroParcela = 1; numeroParcela <= meses; numeroParcela++) {
            double juros = valorAtual * (taxa / 100);
            double amortizacao = valorParcela - juros;
            double saldoDevedor = valorAtual - amortizacao;

            Parcela parcela = new Parcela(numeroParcela, valorParcela, juros, amortizacao, saldoDevedor);
            listaParcelas.add(parcela);

            valorAtual = saldoDevedor;
        }

        return listaParcelas;
    }
}
