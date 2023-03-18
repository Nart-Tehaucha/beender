package com.example.beender;

import static android.view.View.DRAG_FLAG_OPAQUE;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItineraryFragment extends Fragment {

    private List<Day> days;
    AlertDialog deleteDialog = null;


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
        DayAdapter adapter = new DayAdapter(days, view);
        recyclerView.setAdapter(adapter);

        setUpDeleteAttraction(view, adapter);

        return view;
    }

    private void setUpDeleteAttraction(View parent, DayAdapter adapter) {
        View deleteImg = parent.findViewById(R.id.delete_attraction);

        deleteImg.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Determine if this view can accept the dragged data
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            // Indicate that this view can accept the drag data
                            v.setAlpha(0.5f);
                            return true;
                        } else {
                            // This view cannot accept the drag data
                            return false;
                        }

                    case DragEvent.ACTION_DRAG_ENTERED:
                        // Change the trash can view's background color to indicate that the user is over it
                        v.setAlpha(1f);
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        // Reset the trash can view's background color to indicate that the user has left it
                        v.setAlpha(0.5f);
                        return true;

                    case DragEvent.ACTION_DROP:
                        // Get the dropped data
                        int attractionPosition = Integer.parseInt(event.getClipData().getItemAt(0).getText().toString());
                        int dayPosition = Integer.parseInt(event.getClipData().getItemAt(1).getText().toString());

                        showDeleteConfirmationDialog(adapter, attractionPosition, dayPosition);

                        // Reset the trash can view's background color to indicate that the user has dropped the item
                        v.setAlpha(0.5f);
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        // Reset the trash can view's background color to indicate that the drag has ended
                        v.setAlpha(0.5f);
                        return true;

                    default:
                        // An unknown action has occurred
                        return false;
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(DayAdapter adapter, int attractionPosition, int dayPosition) {
        Attraction attraction = days.get(dayPosition).getAttractionList().get(attractionPosition);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_attraction, null);

        TextView messageTextView = view.findViewById(R.id.dialog_message);
        messageTextView.setText(getString(R.string.delete_attraction_message, attraction.getName()));

        Button yesButton = view.findViewById(R.id.btn_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the attraction from the itinerary
                days.get(dayPosition).getAttractionList().remove(attractionPosition);
                adapter.notifyDataSetChanged();

                deleteDialog.dismiss();

            }
        });

        Button noButton = view.findViewById(R.id.btn_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog
                deleteDialog.dismiss();
            }
        });

        builder.setView(view);
        deleteDialog = builder.create();
        deleteDialog.show();
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

        private final View parent;
        private List<Day> dayList;
        private List<AttractionAdapter> adapters;

        public DayAdapter(List<Day> dayList, View view) {
            this.dayList = dayList;
            this.adapters = new ArrayList<>();
            this.parent = view;
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
            AttractionAdapter adapter = new AttractionAdapter(day.getAttractionList(), dayList, position, parent, () -> {
                for (AttractionAdapter modified : adapters) {
                    modified.notifyDataSetChanged();
                }
            });
            adapters.add(adapter);
            holder.attractionRecyclerView.setAdapter(adapter);
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
        private final Runnable onItemMovedListener;
        private final View page;
        private List<Attraction> attractionList;
        private List<Day> daysList;
        private int dayIndex;


        public AttractionAdapter(List<Attraction> attractionList, List<Day> daysList, int dayIndex, View page, Runnable onItemMovedListener) {
            this.attractionList = attractionList;
            this.daysList = daysList;
            this.dayIndex = dayIndex;
            this.page = page;
            this.onItemMovedListener = onItemMovedListener;
        }

        @Override
        public AttractionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attraction_item, parent, false);
            return new AttractionViewHolder(view, this, this.page);
        }

        @Override
        public void onBindViewHolder(AttractionViewHolder holder, int positionPrm) {
            int position = positionPrm;

            holder.itemView.setAlpha(1f);

            if (attractionList.size() == 0) {
                holder.attractionName.setText("No plans!");
                holder.attractionHours.setVisibility(View.GONE);
                holder.attractionImage.setVisibility(View.GONE);
            } else {
                if (position >= attractionList.size()) {
                    holder.attractionName.setVisibility(View.GONE);
                    holder.attractionHours.setVisibility(View.GONE);
                    holder.attractionImage.setVisibility(View.GONE);

                    return;
                }

                Attraction attraction = attractionList.get(position);
                holder.attractionName.setText(attraction.getName());
                holder.attractionHours.setText("🕐 " + attraction.getHours());

                holder.attractionName.setVisibility(View.VISIBLE);
                holder.attractionHours.setVisibility(View.VISIBLE);
                holder.attractionImage.setVisibility(View.VISIBLE);

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public boolean onLongClick(View v) {
                        // Start the drag
                        ClipData.Item attrPos = new ClipData.Item(String.valueOf(position));
                        ClipData.Item dayPos = new ClipData.Item(String.valueOf(dayIndex));
                        ClipData dragData = new ClipData("attraction", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, attrPos);
                        dragData.addItem(dayPos);
                        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v) {
                            private final Drawable shadow = new ColorDrawable(Color.LTGRAY);;



                            @Override
                            public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                                super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
                                getView().setAlpha(0.2f);
                                page.findViewById(R.id.delete_attraction).setVisibility(View.VISIBLE);
                            }

//                                @Override
//                                public void onDrawShadow(Canvas canvas) {
//                                    shadow.setBounds(0, 0, getView().getWidth(), getView().getHeight());
//                                    shadow.draw(canvas);
//                                }
                        };
                        v.startDragAndDrop(dragData, myShadow, null, DRAG_FLAG_OPAQUE);
                        return true;
                    }
                });
//                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.N)
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                            // Start the drag
//                            ClipData.Item attrPos = new ClipData.Item(String.valueOf(position));
//                            ClipData.Item dayPos = new ClipData.Item(String.valueOf(dayIndex));
//                            ClipData dragData = new ClipData("attraction", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, attrPos);
//                            dragData.addItem(dayPos);
//                            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v) {
//                                private final Drawable shadow = new ColorDrawable(Color.LTGRAY);;
//
//
//
//                                @Override
//                                public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
//                                    super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
//                                    getView().setAlpha(0.2f);
//                                }
//
////                                @Override
////                                public void onDrawShadow(Canvas canvas) {
////                                    shadow.setBounds(0, 0, getView().getWidth(), getView().getHeight());
////                                    shadow.draw(canvas);
////                                }
//                            };
//                            v.startDragAndDrop(dragData, myShadow, null, DRAG_FLAG_OPAQUE);
//                            return true;
//                        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
//                            return false;
//                        }else {
//                            return false;
//                        }
//                    }
//                });
            }
        }

        @Override
        public int getItemCount() {
            return attractionList.size() + 1;
        }

        public static class AttractionViewHolder extends RecyclerView.ViewHolder implements View.OnDragListener {
            private final AttractionAdapter mAdapter;
            private final View page;
            ImageView attractionImage;
            TextView attractionName, attractionHours;

            public AttractionViewHolder(View itemView, AttractionAdapter attractionAdapter, View page) {
                super(itemView);
                this.mAdapter = attractionAdapter;
                attractionImage = itemView.findViewById(R.id.attraction_image);
                attractionName = itemView.findViewById(R.id.attraction_name);
                attractionHours = itemView.findViewById(R.id.attraction_hours);
                this.page = page;
                itemView.setOnDragListener(this);
            }

            @Override
            public boolean onDrag(View v, DragEvent event) {
                ViewGroup viewGroup = (ViewGroup) itemView.getParent();
                while (viewGroup != null && !(viewGroup instanceof RecyclerView)) {
                    viewGroup = (ViewGroup) viewGroup.getParent();
                }

                RecyclerView recyclerView = (RecyclerView) viewGroup;

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
                        int fromPosition = Integer.parseInt(event.getClipData().getItemAt(0).getText().toString());
                        int dayIndex = Integer.parseInt(event.getClipData().getItemAt(1).getText().toString());
                        int toPosition = recyclerView.getChildAdapterPosition(v);
                        mAdapter.moveItem(dayIndex, fromPosition, toPosition);
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setAlpha(1f);
                        page.findViewById(R.id.delete_attraction).setVisibility(View.INVISIBLE);
                        v.setBackgroundResource(0);
                        return true;
                    default:
                        return false;
                }
            }
        }

        public void moveItem(int dayIndex, int fromPosition, int toPosition) {
            ArrayList<Attraction> origAttractionList = daysList.get(dayIndex).getAttractionList();
            Attraction attraction = origAttractionList.get(fromPosition);

            origAttractionList.remove(fromPosition);
            attractionList.add(toPosition, attraction);
//            notifyDataSetChanged();
            onItemMovedListener.run();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
//            Attraction fromAttraction = attractionList.get(fromPosition);
//            attractionList.remove(fromAttraction);
//            attractionList.add(toPosition, fromAttraction);
//            notifyItemMoved(fromPosition, toPosition);
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