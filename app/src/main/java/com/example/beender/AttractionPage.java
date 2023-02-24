package com.example.beender;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.beender.model.ItemAdditionalData;
import com.example.beender.model.ItemModel;
import com.example.beender.model.Review;

import org.w3c.dom.Text;

import java.util.List;

import io.opencensus.trace.Span;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttractionPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttractionPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    ItemModel attractionArg;

    public AttractionPage() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment AttractionPage.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static AttractionPage newInstance(String param1, String param2) {
//        AttractionPage fragment = new AttractionPage();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            attractionArg = (ItemModel) getArguments().getSerializable("attraction");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attraction_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeViews(view);

    }

    private void setUpStars(View parent) {
        LinearLayout starsLayout = (LinearLayout)parent.findViewById(R.id.stars_layout);

        double attractionRating = attractionArg.getRatingAsDouble();
        for (int i = 0; i < attractionRating; i++) {
            ImageView star = new ImageView(getContext());
            star.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), com.google.android.libraries.places.R.color.quantum_yellow), PorterDuff.Mode.SRC_IN));
            int sizeInDp = 30;
            int sizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());
            star.setLayoutParams(new ViewGroup.LayoutParams(sizeInPx, sizeInPx));

            if (attractionRating - i > 1) {
                star.setImageResource(R.drawable.star_filled);
            } else {
                star.setImageResource(R.drawable.star_half);
            }


            starsLayout.addView(star);
        }
    }

    private void setUpReviews(View parent) {
        ItemAdditionalData additionalData = attractionArg.fetchAdditionalData();

        List<Review> reviews = additionalData.getReviews();
        LinearLayout reviewsLayout = (LinearLayout)parent.findViewById(R.id.reviews_layout);
        for (Review review : reviews) {

            ImageView profilePicture = new ImageView(getContext());
            profilePicture.setImageBitmap(review.getProfilePicture());


            // create a SpannableStringBuilder and append the image, author name and rating
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(new SpannableString(" "));

            // Append the author name in bold
            String authorName = review.getAuthorName() + " ";
            SpannableStringBuilder authorBuilder = new SpannableStringBuilder(authorName);
            authorBuilder.setSpan(new RelativeSizeSpan(1.2f), 0, authorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            authorBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, authorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableStringBuilder starsAndDateBuilder = new SpannableStringBuilder();

            // Append the rating stars
            int rating = review.getRating();
            String stars = " ";
            for (int i = 0; i < rating; i++) {
                stars += "★";
            }
            stars += "  ";


            ForegroundColorSpan span = new ForegroundColorSpan(Color.YELLOW);
            starsAndDateBuilder.append(new SpannableString(stars), span, 0);


            String relativeTimeDescription = review.getRelativeTimeDescription() ;
            starsAndDateBuilder.append(relativeTimeDescription);

            // Append the review text

            builder.append(authorBuilder);
            builder.append(starsAndDateBuilder);

            // Create a TextView and set the text
            TextView reviewHeadTextView = new TextView(getContext());
            reviewHeadTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            reviewHeadTextView.setPadding(10, 10, 10, 20);
            reviewHeadTextView.setTextSize(14);
            reviewHeadTextView.setText(builder);

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.addView(profilePicture);
            linearLayout.addView(reviewHeadTextView);

            reviewsLayout.addView(linearLayout);

            TextView reviewTextView = new TextView(getContext());
            reviewTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            reviewTextView.setPadding(10, 10, 10, 50);
            reviewTextView.setTextSize(14);
            reviewTextView.setText(review.getText());

            reviewsLayout.addView(reviewTextView);
        }
    }

    private void setAllImagesByCurrentPosition(View parent, List<Bitmap> images, int currentPosition) {
        ImageView mainImage = (ImageView)parent.findViewById(R.id.main_image);
        mainImage.setImageBitmap(images.get(currentPosition));

        LinearLayout thumbnailLayout = parent.findViewById(R.id.thumbnail_layout);
        int width = getResources().getDisplayMetrics().widthPixels / 3; // get 1/3rd of the screen width
        int height = (int) (width * 0.5); // set the height proportional to the width

        int padding = 4; // in dp
        float scale = getResources().getDisplayMetrics().density;
        int pixelPadding = (int) (padding * scale + 0.5f);

        for (int i = 0; i < images.size(); i++) {
            Bitmap image = images.get(i);
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(pixelPadding, 0, pixelPadding, 0);
            imageView.setImageBitmap(image);


            int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllImagesByCurrentPosition(parent, images, finalI);
            }
        });

            thumbnailLayout.addView(imageView);
        }


//        //TODO: This duplicate code is ugly, better to create an array for all the thumbails and do this in a loop. But I'm feeling pretty lazy now.
//        final int nextPosition1 = (currentPosition + 1) % images.size();
//        ImageView thumbnail1 = (ImageView)parent.findViewById(R.id.thumbnail_1);
//        thumbnail1.setImageBitmap(images.get(nextPosition1));
//        thumbnail1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setAllImagesByCurrentPosition(parent, images, nextPosition1);
//            }
//        });
//
//        final int nextPosition2 = (currentPosition + 2) % images.size();
//        ImageView thumbnail2 = (ImageView)parent.findViewById(R.id.thumbnail_2);
//        thumbnail2.setImageBitmap(images.get(nextPosition2));
//        thumbnail2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setAllImagesByCurrentPosition(parent, images, nextPosition2);
//            }
//        });
//
//        final int nextPosition3 = (currentPosition + 3) % images.size();
//        ImageView thumbnail3 = (ImageView)parent.findViewById(R.id.thumbnail_3);
//        thumbnail3.setImageBitmap(images.get(nextPosition3));
//        thumbnail3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setAllImagesByCurrentPosition(parent, images, nextPosition3);
//            }
//        });

    }

    private void initializeViews(View parent) {
        TextView attractionTitle = (TextView)parent.findViewById(R.id.attraction_title);
        attractionTitle.setText(attractionArg.getName());
        ImageView attractionImage = (ImageView)parent.findViewById(R.id.main_image);

        List<Bitmap> images = attractionArg.fetchAdditionalData().getImages();

        if (images.size() == 0) {
            attractionImage.setImageBitmap(attractionArg.getImage());
        } else {
            setAllImagesByCurrentPosition(parent, images, 0);
        }


        setUpStars(parent);
        setUpReviews(parent);

        TextView attractionDescription = (TextView)parent.findViewById(R.id.attraction_description);
        TextView readMore = (TextView)parent.findViewById(R.id.read_more);

        String fullDescription = attractionArg.fetchAdditionalData().getDescription();
        int index = fullDescription.indexOf("\n"); // Find the index of the first double line break
        String description;
        if (index >= 0) {
            description = fullDescription.substring(0, index); // Get the text before the first line break

            readMore.setVisibility(View.VISIBLE);
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // uncomment this to redirect to wikipedia on click
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/" + attractionArg.getName()));
//                    startActivity(intent);

                    attractionDescription.setText(attractionArg.fetchAdditionalData().getDescription());

                }
            });
        } else {
            readMore.setVisibility(View.INVISIBLE);
            description = fullDescription; // There are no double line breaks, so use the entire text as the first paragraph
        }

        attractionDescription.setText(description);




    }
}