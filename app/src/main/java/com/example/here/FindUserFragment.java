package com.example.here;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.here.models.UserData;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindUserFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    List<UserData> users;
    UserAdapter userAdapter;
    ProgressBar progressBar;



    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private List<UserData> users;

        public UserAdapter(List<UserData> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user_searched, parent, false);
            return new UserViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            UserData userData = users.get(position);
//            holder.avatarView.setResource(userData.getAvatar());
            holder.avatarView.setImageResource(R.drawable.ic_round_person_24);
            holder.nicknameTextView.setText(userData.getNick());
            holder.userId = userData.getUser();
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public class UserViewHolder extends RecyclerView.ViewHolder {
            TextView nicknameTextView;
            ImageView avatarView;
            int userId;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                nicknameTextView = itemView.findViewById(R.id.user_name);
                avatarView = itemView.findViewById(R.id.user_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserData userData = users.get(getAdapterPosition());
                        showProfile(userId);
                    }
                });
            }

//            public void bind(UserData userData) {
//                userId = userData.getId();
//            }

        }
    }

    private void showProfile(int userId) {
        Fragment fragment = AnotherUserProfileFragment.newInstance(userId);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }


    public void reloadDataset(List<UserData> newUsers) {
        users.clear();
        users.addAll(newUsers);
        userAdapter.notifyDataSetChanged();

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_user, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new PersonFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        this.recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.users = new ArrayList<>();
        this.userAdapter = new UserAdapter(users);
        this.recyclerView.setAdapter(userAdapter);

        this.progressBar = view.findViewById(R.id.progressBar);

        SearchView searchView = view.findViewById(R.id.searchView_find_users);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(requireContext(), "Search query: " + query, Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
                SharedPreferences sp = getActivity().getSharedPreferences("msb", Context.MODE_PRIVATE);

                Call<List<UserData>> call = apiInterface.findUsersByNickname("Token " + sp.getString("token", ""), query);
                call.enqueue(new Callback<List<UserData>>() {
                    @Override
                    public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                        List<UserData> newUsers = response.body();
                        reloadDataset(newUsers);
                    }

                    @Override
                    public void onFailure(Call<List<UserData>> call, Throwable t) {

                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change if needed
                return false;
            }
        });

        return view;
    }
}
