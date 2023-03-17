package com.example.beender;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beender.model.UserTrip;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItineraryFragment extends Fragment {

    private List<Day> days;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);

        // Create mock data for days
        days = new ArrayList<>();
        Day day1 = new Day("Day 1");
        day1.addAttraction(new Attraction("Attraction 1", "10am - 2pm"));
        day1.addAttraction(new Attraction("Attraction 2", "2pm - 4pm"));
        day1.addAttraction(new Attraction("Attraction 3", "5pm - 7pm"));

        days.add(day1);

        Day day2 = new Day("Day 2");
        day2.addAttraction(new Attraction("Attraction 4", "9am - 11am"));
        day2.addAttraction(new Attraction("Attraction 5", "12pm - 3pm"));

        days.add(day2);

        Day day3 = new Day("Day 3");
        days.add(day3);

        Day day4 = new Day("Day 4");
        day4.addAttraction(new Attraction("Attraction 6", "12:30pm - 1pm"));

        days.add(day4);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = view.findViewById(R.id.day_recycler_view);
        DayAdapter adapter = new DayAdapter(days);
        recyclerView.setAdapter(adapter);

        return view;
    }
    
    private static class Attraction {

        private final String name;
        private final String hours;

        public Attraction(String name, String hours) {
            this.name = name;
            this.hours = hours;
        }

        public String getName() {
            return this.name;
        }

        public String getHours() {
            return hours;
        }
    }
    private static class Day {
        private final String title;
        public ArrayList<Attraction> attractions;

        public Day(String title) {
            this.title = title;
            this.attractions = new ArrayList<>();
        }

        public void addAttraction(Attraction attraction) {
            attractions.add(attraction);
        }

        public String getTitle() {
            return title;
        }

        public ArrayList<Attraction> getAttractionList() {
            return attractions;
        }
    }

    public static class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

        private List<Day> dayList;

        public DayAdapter(List<Day> dayList) {
            this.dayList = dayList;
        }

        @Override
        public DayViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
            return new DayViewHolder(view);
        }

        @Override
        public void onBindViewHolder( DayViewHolder holder, int position) {
            Day day = dayList.get(position);
            holder.dayTitle.setText(day.getTitle());
            holder.attractionRecyclerView.setAdapter(new AttractionAdapter(day.getAttractionList()));
        }

        @Override
        public int getItemCount() {
            return dayList.size();
        }

        public static class DayViewHolder extends RecyclerView.ViewHolder {

            private TextView dayTitle;
            private RecyclerView attractionRecyclerView;

            public DayViewHolder(View itemView) {
                super(itemView);
                dayTitle = itemView.findViewById(R.id.day_title);
                attractionRecyclerView = itemView.findViewById(R.id.attraction_recycler_view);
            }
        }
    }

    public static class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> implements AttractionItemTouchHelper.ItemTouchHelperAdapter {
        private List<Attraction> attractionList;

        public AttractionAdapter(List<Attraction> attractionList) {
            this.attractionList = attractionList;
        }

        @Override
        public AttractionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attraction_item, parent, false);
            return new AttractionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AttractionViewHolder holder, int position) {
            Log.d("AttractionAdapter", "Attraction name: " + attractionList.get(position).getName()); // add this log to check if attractionList is being properly passed in

            Attraction attraction = attractionList.get(position);
//            holder.attractionImage.setImageResource(attraction.getImageResource());
            holder.attractionName.setText(attraction.getName());
            holder.attractionHours.setText("🕐 " + attraction.getHours());

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Start the drag
                        ClipData.Item item = new ClipData.Item(String.valueOf(position));
                        ClipData dragData = new ClipData("attraction", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                        v.startDragAndDrop(dragData, myShadow, null, 0);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return attractionList.size();
        }

        public static class AttractionViewHolder extends RecyclerView.ViewHolder implements View.OnDragListener {
            ImageView attractionImage;
            TextView attractionName, attractionHours;

            public AttractionViewHolder(View itemView) {
                super(itemView);
                attractionImage = itemView.findViewById(R.id.attraction_image);
                attractionName = itemView.findViewById(R.id.attraction_name);
                attractionHours = itemView.findViewById(R.id.attraction_hours);
                itemView.setOnDragListener(this);
            }

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundResource(R.drawable.gradation_black);
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundResource(0);
                        return true;
                    case DragEvent.ACTION_DROP:
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundResource(0);
                        return true;
                    default:
                        return false;
                }
            }
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Attraction fromAttraction = attractionList.get(fromPosition);
            attractionList.remove(fromAttraction);
            attractionList.add(toPosition, fromAttraction);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }

    public static class AttractionItemTouchHelper extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter mAdapter;

        public AttractionItemTouchHelper(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Do nothing
        }

        public interface ItemTouchHelperAdapter {
            boolean onItemMove(int fromPosition, int toPosition);
        }
    }
}