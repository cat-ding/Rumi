package com.example.rumi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rumi.ComposeActivity;
import com.example.rumi.models.Post;
import com.example.rumi.adapters.PostsAdapter;
import com.example.rumi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class PostsFragment extends Fragment implements FiltersBottomSheetDialog.BottomSheetListener {

    // TODO: MAKE A CONSTANTS CLASS!!
    public static final String TAG = "PostsFragment";
    private static final int CREATE_POST_REQUEST = 55;
    public static final int LIKE_POST_REQUEST = 25;
    private static final int BOTTOM_SHEET_REQUEST_CODE = 5;

    public static final String SORT_DEFAULT = "Recent (Default)";
    private static final String SORT_POPULARITY = "Popularity";
    private static final String SORT_RENT_HIGH_TO_LOW = "Rent (High to Low)";
    private static final String SORT_RENT_LOW_TO_HIGH = "Rent (Low to High)";

    public static final int LOOKING_FOR_DEFAULT = -1; // any
    public static final int LOOKING_FOR_PLACE = 0; // looking for a place
    public static final int LOOKING_FOR_TENANT = 1; // looking for tenant

    public static final int FILTER_ROOMS_DEFAULT = 0; // any number of rooms

    public static final int FURNISHED_DEFAULT = -1; // include both furnished and unfurnished
    public static final int FURNISHED_YES = 0;
    public static final int FURNISHED_NO = 1;

    protected RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;
    private List<Post> allPostsCopy = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postsRef = db.collection(Post.KEY_POSTS);
    private androidx.appcompat.widget.Toolbar toolbar;
    private SwipeRefreshLayout swipeContainer;
    private ImageView ivFilter;
    private TextView tvFilter;

    // to keep track of last filter selections
    private String currSort = SORT_DEFAULT;
    private int currLookingFor = LOOKING_FOR_DEFAULT;
    private int currNumRooms = FILTER_ROOMS_DEFAULT;
    private int currFurnished = FURNISHED_DEFAULT;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // swipeContainer refresh configs
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (currSort.equals(SORT_DEFAULT)) {
                    loadPosts();
                } else if (currSort.equals(SORT_POPULARITY)) {
                    loadPostsPopularity();
                } else if (currSort.equals(SORT_RENT_HIGH_TO_LOW)) {
                    loadPostsHighToLow();
                } else if (currSort.equals(SORT_RENT_LOW_TO_HIGH)) {
                    loadPostsLowToHigh();
                }
            }
        });

        rvPosts = view.findViewById(R.id.rvPosts);
        tvFilter = view.findViewById(R.id.tvFilter);
        ivFilter = view.findViewById(R.id.ivFilter);
        tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilters();
            }
        });
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilters();
            }
        });

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts, this);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);

        loadPosts();
    }

    // default load posts by most recent
    private void loadPosts() {
        postsRef.orderBy(Post.KEY_CREATED_AT, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addPostsToAdapter(queryDocumentSnapshots);
                    }
                });
    }

    private void loadPostsPopularity() {
        postsRef.orderBy(Post.KEY_POPULARITY, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addPostsToAdapter(queryDocumentSnapshots);
                    }
                });
    }

    private void loadPostsHighToLow() {
        postsRef.orderBy(Post.KEY_RENT, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addPostsToAdapter(queryDocumentSnapshots);
                    }
                });
    }

    private void loadPostsLowToHigh() {
        postsRef.orderBy(Post.KEY_RENT, Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addPostsToAdapter(queryDocumentSnapshots);
                    }
                });
    }

    private void addPostsToAdapter(QuerySnapshot queryDocumentSnapshots) {
        adapter.clear();
        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            Post post = documentSnapshot.toObject(Post.class);

            allPosts.add(post);
            // perform a deep copy to use when filtering
            allPostsCopy.clear();
            for (Post p : allPosts) {
                Post newPost = new Post(p.getCreatedAt(), p.getLikes(), p.getPopularity(),
                        p.getTitle(), p.getDescription(), p.getStartMonth(), p.getUserId(),
                        p.getNumRooms(), p.getDuration(), p.getRent(), p.isFurnished(),
                        p.isLookingForHouse(), p.getStartDate(), p.getEndDate(), p.getPhotoUrl(),
                        p.getPostId(), p.getName(), p.getAddress(), p.getLatitude(), p.getLongitude());
                allPostsCopy.add(newPost);
            }
        }
        applyFilters(currLookingFor, currNumRooms, currFurnished);
        adapter.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);
    }

    private void openFilters() {
        FiltersBottomSheetDialog filtersDialog =
                FiltersBottomSheetDialog.newInstance(currSort, currLookingFor, currNumRooms, currFurnished);
        filtersDialog.setTargetFragment(PostsFragment.this, BOTTOM_SHEET_REQUEST_CODE);
        filtersDialog.show(getFragmentManager(), "FiltersBottomSheetDialog");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_compose, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getContext(), ComposeActivity.class);
        startActivityForResult(intent, CREATE_POST_REQUEST);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_POST_REQUEST) {
            Parcelable newPostParcel = data.getParcelableExtra("newPost");

            if (newPostParcel != null) {
                Post newPost = Parcels.unwrap(newPostParcel);

                allPosts.add(0, newPost);
                adapter.notifyItemInserted(0);
                rvPosts.smoothScrollToPosition(0);
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == LIKE_POST_REQUEST) {
            Parcelable updatedPostParcel = data.getParcelableExtra("updatedPost");

            if (updatedPostParcel != null) {
                Post updatedPost = Parcels.unwrap(updatedPostParcel);

                // find adapter position (where the post was)
                int position = -1;
                for (int i = 0; i < allPosts.size(); i++) {
                    if (allPosts.get(i).getPostId().equals(updatedPost.getPostId())) {
                        position = i;
                        break;
                    }
                }
                allPosts.remove(position);
                allPosts.add(position, updatedPost);
                adapter.notifyItemChanged(position);
            }
        }
    }

    private void applyFilters(int filterLookingFor, int filterNumRooms, int filterFurnished) {

        boolean boolLookingForPlace = true, booleanFurnished = true;
        if (filterLookingFor == LOOKING_FOR_TENANT)
            boolLookingForPlace = false;
        if (filterFurnished == FURNISHED_NO)
            booleanFurnished = false;

        Iterator<Post> iterator = allPosts.iterator();
        while (iterator.hasNext()) {
            Post p = iterator.next();
            if (filterNumRooms != 0) {
                if (p.getNumRooms() != filterNumRooms) {
                    iterator.remove();
                    continue; // prevents duplicate deletions of the same iteration
                }
            }
            if (filterLookingFor != -1) {
                if (!(p.isLookingForHouse() == boolLookingForPlace)) {
                    iterator.remove();
                    continue;
                }
            }
            if (filterFurnished != -1) {
                if (!(p.isFurnished() == booleanFurnished)) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void sendFilterSelections(final String sortType, int filterLookingFor, int filterNumRooms, int filterFurnished) {

        if (currLookingFor != filterLookingFor || currNumRooms != filterNumRooms || currFurnished != filterFurnished) {
            allPosts.clear();
            // perform a deep copy of old posts
            for (Post p : allPostsCopy) {
                Post newPost = new Post(p.getCreatedAt(), p.getLikes(), p.getPopularity(),
                        p.getTitle(), p.getDescription(), p.getStartMonth(), p.getUserId(),
                        p.getNumRooms(), p.getDuration(), p.getRent(), p.isFurnished(),
                        p.isLookingForHouse(), p.getStartDate(), p.getEndDate(), p.getPhotoUrl(),
                        p.getPostId(), p.getName(), p.getAddress(), p.getLatitude(), p.getLongitude());
                allPosts.add(newPost);
            }

            if (filterLookingFor != LOOKING_FOR_DEFAULT
                    || filterNumRooms != FILTER_ROOMS_DEFAULT
                    || filterFurnished != FURNISHED_DEFAULT)
                applyFilters(filterLookingFor, filterNumRooms, filterFurnished);
            currLookingFor = filterLookingFor;
            currNumRooms = filterNumRooms;
            currFurnished = filterFurnished;
        }

        Collections.sort(allPosts, new Comparator<Post>() {
            public int compare(Post postOne, Post postTwo) {
                if (sortType.equals(SORT_DEFAULT)) {
                    return postTwo.getCreatedAt().compareTo(postOne.getCreatedAt());
                } else if (sortType.equals(SORT_POPULARITY)) {
                    return postTwo.getPopularity() - postOne.getPopularity();
                } else if (sortType.equals(SORT_RENT_HIGH_TO_LOW)) {
                    return postTwo.getRent() - postOne.getRent();
                } else {
                    return postOne.getRent() - postTwo.getRent();
                }
            }
        });

        currSort = sortType;

        adapter.notifyDataSetChanged();
        rvPosts.smoothScrollToPosition(0);
    }
}
