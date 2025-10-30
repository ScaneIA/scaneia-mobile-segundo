package com.example.scaneia.analises;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.scaneia.Perfil;
import com.example.scaneia.R;
import com.example.scaneia.databinding.FragmentAnalisesBinding;

public class AnalisesFragment extends Fragment {

    private FragmentAnalisesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnalisesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView profile = root.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Perfil.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}