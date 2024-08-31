package com.example.appemprestimo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.appemprestimo.entidades.Parcela;

import java.util.List;
import java.util.Locale;

public class EmprestimoAdapter extends ArrayAdapter<Parcela> {

    private final int layoutResource;

    public EmprestimoAdapter(@NonNull Context context, int resource, @NonNull List<Parcela> parcelas) {
        super(context, resource, parcelas);
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvNumeroParcela = convertView.findViewById(R.id.tvNum);
            viewHolder.tvValorParcela = convertView.findViewById(R.id.tvValorParcela);
            viewHolder.tvJuros = convertView.findViewById(R.id.tvJuros2);
            viewHolder.tvAmortizacao = convertView.findViewById(R.id.tvAmort);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Parcela parcela = getItem(position);
        if (parcela != null) {
            viewHolder.tvNumeroParcela.setText(String.valueOf(parcela.getNum()));
            viewHolder.tvValorParcela.setText(formatarValor(parcela.getPrestacao()));
            viewHolder.tvJuros.setText(formatarValor(parcela.getJuros()));
            viewHolder.tvAmortizacao.setText(formatarValor(parcela.getAmort()));
        }

        if (position == (getCount() / 2 - 1)) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange));
        }

        return convertView;
    }

    private String formatarValor(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private static class ViewHolder {
        TextView tvNumeroParcela;
        TextView tvValorParcela;
        TextView tvJuros;
        TextView tvAmortizacao;
    }
}
