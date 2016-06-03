package com.globant.android101.chuck_norris_jokes.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.globant.android101.R;
import com.globant.android101.chuck_norris_jokes.api.ChuckNorrisJokesApi;
import com.globant.android101.chuck_norris_jokes.api.JokeResponse;
import com.globant.android101.chuck_norris_jokes.ui.adapter.JokeAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JokeListFragment extends Fragment {

    // Constants
    private static final int MAX_JOKES_PER_REQUEST = 15;

    // Attributes
    private RecyclerView rvJokes;
    private JokeAdapter adapter;
    private ProgressBar progressBar;

    private ChuckNorrisJokesApi api;

    private OnJokeSelectedListener listener;

    /**
     * Factory method to create and to return a JokeListFragment.
     */
    public static JokeListFragment newInstance() {
        return new JokeListFragment();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnJokeSelectedListener {
        void onJokeSelected(int postion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joke_list, container, false);

        bindViews(view);

        return view;
    }

    private void bindViews(View view) {
        // Recycler view
        rvJokes = (RecyclerView) view.findViewById(R.id.rv_jokes);

        // Layout manager
        rvJokes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter
        adapter = new JokeAdapter();
        rvJokes.setAdapter(adapter);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJokeSelectedListener) {
            listener = (OnJokeSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnJokeSelectedListener.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        showProgressBar();
        getJokes();
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getJokes() {
        api = createApi();

        Call<JokeResponse> call = api.getRandomJokes(MAX_JOKES_PER_REQUEST);
        call.enqueue(getJokesCallback());
    }

    @NonNull
    private Callback<JokeResponse> getJokesCallback() {
        return new Callback<JokeResponse>() {
            @Override
            public void onResponse(Call<JokeResponse> call, Response<JokeResponse> response) {
                hideProgressBar();
                adapter.addJokes(response.body().getJokes());
            }

            @Override
            public void onFailure(Call<JokeResponse> call, Throwable t) {
                hideProgressBar();
                Toast.makeText(getContext(), R.string.error_msg_jokes, Toast.LENGTH_LONG).show();
            }
        };
    }

    private ChuckNorrisJokesApi createApi() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ChuckNorrisJokesApi.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(ChuckNorrisJokesApi.class);
        }
        return api;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
