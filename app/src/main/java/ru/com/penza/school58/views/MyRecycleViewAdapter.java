package ru.com.penza.school58.views;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.com.penza.school58.R;
import ru.com.penza.school58.datamodel.Card;
import ru.com.penza.school58.datamodel.Message;
import ru.com.penza.school58.web.ApiUtils;
import ru.com.penza.school58.web.SOService;


public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.CardViewHolder> {

    private static final String ACT = "FreeCheckBalance" ;
    private static final String BALANCE = "balance" ;
    List<Card> cards;
    private OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(Card card, CardViewHolder holder);
    }


    public MyRecycleViewAdapter(List<Card> cards, OnItemClickListener listener, Context context) {
        this.cards=cards;
        this.listener=listener;
        this.context = context;
    }



    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_card, parent, false);
        return new CardViewHolder(view);

    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder holder, final int position) {
        holder.name.setText(cards.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(cards.get(holder.getAdapterPosition()), holder);

            }
        });
        holder.message.setText(cards.get(position).getMessage());
        String imageURL = cards.get(position).getImageURL();
        if (imageURL == null) {
            Picasso.with(context).load(R.drawable.student).transform(new CropCircleTransformation())
                    .into(holder.imageView);
        } else {
            Picasso.with(context).load(Uri.parse(imageURL)).transform(new CropCircleTransformation())
                    .into(holder.imageView);
        }
        getBallance(holder, position);

    }

    private void getBallance(final CardViewHolder holder, int position) {
        SOService service = ApiUtils.getSOService();
        service.getAnswer(cards.get(position).getCardNumber(), ACT).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                String message;
                String type = response.body().getType();
                if(type.equals(BALANCE)) {
                    message = formatMessage(Html.fromHtml(response.body().getText()).toString());
                } else {
                    message = response.body().getText();
                }
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.message.setText(message);

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    private String formatMessage(String string) {
        Pattern pattern = Pattern.compile("\n|\n\n");
        String[] split = pattern.split(string);
        String result = "";
        for (String s : split){
            if (!s.equals("Баланс:") && !s.equals("")){
                result = result+s+"\n";
            }
        }
        return result;
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name)
        public TextView name;
        @BindView(R.id.message)
        public TextView message;
        @BindView(R.id.progress)
        public ProgressBar progressBar;
        @BindView(R.id.image)
        public ImageView imageView;

        public CardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
