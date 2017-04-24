package ru.azovtsev.test.yandextest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LanguageActivity extends AppCompatActivity {

	private ArrayList<String> languages;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_language);

		Intent intent = this.getIntent();
		setTitle(intent.getStringExtra("title"));

		if(intent.hasExtra("languages")) {
			languages = intent.getStringArrayListExtra("languages");
		}


		ListView listView = (ListView) findViewById(R.id.listView);

		listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.language_item, languages));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String itemClicked = ((TextView) view).getText().toString();
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra("lang", itemClicked);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}
}