package ru.com.penza.school58.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.com.penza.school58.R;
import ru.com.penza.school58.datamodel.Card;


public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.CardViewHolder> {

    List<Card> cards;
    private Context context;

    public MyRecycleViewAdapter(List<Card> cards, Context context) {
        this.cards=cards;
        this.context=context;
    }



    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_card, parent, false);
        return new CardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.name.setText(cards.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name)
        public TextView name;
        public CardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
