package br.com.guiainvestimento.fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.guiainvestimento.BuildConfig;
import br.com.guiainvestimento.R;
import butterknife.ButterKnife;

public class AboutFragment extends BaseFragment{
    private static final String LOG_TAG = AboutFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        TextView appVersion = (TextView) view.findViewById(R.id.app_version);
        String versionName = BuildConfig.VERSION_NAME;

        Resources res = getResources();
        String versionText = String.format(res.getString(R.string.app_version), versionName);
        appVersion.setText(versionText);
        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return view;
    }
}
