package ru.azovtsev.test.yandextest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class Tab1Fragment extends Fragment {

    private static final String URL_LANG = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?ui=ru&key=trnsl.1.1.20170424T121336Z.029f12a8fb35583b.cd104cd98bea41f0dc1af275bdec50dba1f1988c";
    private static final String URL_TRANSLATE = "https://translate.yandex.net/api/v1.5/tr.json/translate?format=plain&key=trnsl.1.1.20170424T121336Z.029f12a8fb35583b.cd104cd98bea41f0dc1af275bdec50dba1f1988c";

    private ArrayList<String> languagesKeys = new ArrayList<>();
    private ArrayList<String> languageStrings = new ArrayList<>();

    private Button buttonFrom;
    private Button buttonTo;
    private Button buttonTranslate;

    private String from = "en";
    private String to = "ru";

    private EditText inputText;
    private TextView resultText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1_layout,container,false);

        inputText = (EditText) view.findViewById(R.id.input_text);
        resultText = (TextView) view.findViewById(R.id.result_text);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.fragment1_title);

        buttonFrom = (Button) getActivity().findViewById(R.id.button_lang1);
        buttonFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguageActivity.class);
                intent.putExtra("title", "Выбор языка");
                intent.putExtra("languages", languageStrings);
                startActivityForResult(intent, 0);
             }
        });

        buttonTo = (Button) getActivity().findViewById(R.id.button_lang2);
        buttonTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguageActivity.class);
                intent.putExtra("title", "Выбор перевода");
                intent.putExtra("languages", languageStrings);
                startActivityForResult(intent, 1);
            }
        });

        buttonTranslate = (Button) view.findViewById(R.id.button_translate);
        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputText.getText().toString().isEmpty()) {
                    new getTranslate().execute();
                }
            }
        });

        new getLangs().execute();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        String res = data.getStringExtra("lang");
        switch (requestCode) {
            case 0:
                buttonFrom.setText(res);
                from =  languagesKeys.get(languageStrings.indexOf(res));
                break;
            case 1:
                buttonTo.setText(res);
                to =  languagesKeys.get(languageStrings.indexOf(res));
                break;
        }
    }

    private class getLangs extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(URL_LANG);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            try {

                JSONObject json = new JSONObject(response);
                JSONArray dirs = json.getJSONArray("dirs");
                JSONObject langs = json.getJSONObject("langs");

                Iterator<?> keys = langs.keys();

                languagesKeys = new ArrayList<>();
                languageStrings = new ArrayList<>();

                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    languagesKeys.add(key);
                    languageStrings.add(langs.getString(key));
                }

                buttonFrom.setText(languageStrings.get(languagesKeys.indexOf(from)));
                buttonTo.setText(languageStrings.get(languagesKeys.indexOf(to)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class getTranslate extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(URL_TRANSLATE + "&lang="+ from+ "-" + to + "&text=" + inputText.getText().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            try {
                JSONObject json = new JSONObject(response);
                JSONArray text = json.getJSONArray("text");
                if (text.length() > 0) {
                    resultText.setText(text.get(0).toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}