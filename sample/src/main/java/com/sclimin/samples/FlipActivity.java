package com.sclimin.samples;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class FlipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getIntent().getIntExtra("LAYOUT_ID", R.layout.activity_flip_horizontal));
        RecyclerView rv = findViewById(R.id.recycle_view);
        rv.setAdapter(new Adapter());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTitle;
        private final View mBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mBg = itemView.findViewById(R.id.bg);
        }

        public void setTitle(String title) {
            mTitle.setText(title);
        }

        public void setBg(int color) {
            mBg.setBackgroundColor(color);
        }
    }

    public static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        public final Pair<String, Integer>[] mItems;

        public Adapter() {
            mItems = new Pair[]{
                    new Pair<>("YELLOW", Color.YELLOW),
                    new Pair<>("GREEN", Color.GREEN),
                    new Pair<>("MAGENTA", Color.MAGENTA),
                    new Pair<>("YELLOW1", Color.YELLOW),
                    new Pair<>("GREEN1", Color.GREEN),
                    new Pair<>("MAGENTA1", Color.MAGENTA),
                    new Pair<>("YELLOW2", Color.YELLOW),
                    new Pair<>("GREEN2", Color.GREEN),
                    new Pair<>("MAGENTA2", Color.MAGENTA),
                    new Pair<>("YELLOW3", Color.YELLOW),
                    new Pair<>("GREEN3", Color.GREEN),
                    new Pair<>("MAGENTA3", Color.MAGENTA)
            };
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.page_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setTitle("第" + (position + 1) + "页\n" + mItems[position].first);
            holder.setBg(mItems[position].second);
        }

        @Override
        public int getItemCount() {
            return mItems.length;
        }
    }
}
