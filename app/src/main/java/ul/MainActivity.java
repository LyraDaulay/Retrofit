package ul;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.retrofit.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import data.response.CustomerReviewsItem;
import data.response.RestaurantResponse;
import data.response.Restaurant;
import data.retrofit.ApiConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final String RESTAURANT_ID = "uewq1zg2zlskfw1e867";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Sembunyikan ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Atur LayoutManager dan ItemDecoration untuk RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvReview.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.rvReview.addItemDecoration(itemDecoration);

        // Panggil metode untuk mencari dan menampilkan data restoran
        findRestaurant();

        // Set listener untuk tombol "send"
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil metode untuk mengirim review
                sendReview();
            }
        });
    }

    private void findRestaurant() {
        showLoading(true);

        // Mendapatkan data restoran menggunakan Retrofit
        Call<RestaurantResponse> client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID);
        client.enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Restaurant restaurant = response.body().getRestaurant();
                        setRestaurantData(restaurant);
                        List<CustomerReviewsItem> customerReviews = restaurant.getCustomerReviews();
                        setReviewData(customerReviews);
                    }
                } else {
                    if (response.body() != null) {
                        Log.e(TAG, "onFailure: " + response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void setRestaurantData(@NonNull Restaurant restaurant) {
        binding.tvTitle.setText(restaurant.getName());
        binding.tvDescription.setText(restaurant.getDescription());
        Glide.with(MainActivity.this)
                .load("https://restaurant-api.dicoding.dev/images/large/" + restaurant.getPictureId())
                .into(binding.ivPicture);
    }

    private void setReviewData(List<CustomerReviewsItem> customerReviews) {
        ArrayList<String> listReview = new ArrayList<>();
        for (CustomerReviewsItem review : customerReviews) {
            listReview.add(review.getReview() + "\n- " + review.getName());
        }

        ReviewAdapter adapter = new ReviewAdapter(listReview);
        binding.rvReview.setAdapter(adapter);
        binding.edReview.setText("");
    }

    private void sendReview() {
        // Dapatkan teks dari TextInputEditText
        String reviewText = binding.edReview.getText().toString().trim();

        // Cek apakah review tidak kosong
        if (!reviewText.isEmpty()) {
            // Lakukan pengiriman review atau tindakan yang sesuai
            // Contoh: Tampilkan review di Logcat
            Log.d(TAG, "Review: " + reviewText);

            // Beritahu pengguna bahwa review telah terkirim
            Toast.makeText(this, "Review sent: " + reviewText, Toast.LENGTH_SHORT).show();

            // Setelah melakukan tindakan, bisa melakukan hal lain seperti membersihkan input
            binding.edReview.setText("");
        } else {
            // Jika review kosong, beri peringatan kepada pengguna
            Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}
