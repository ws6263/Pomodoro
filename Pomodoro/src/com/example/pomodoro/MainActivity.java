package com.example.pomodoro;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	/* Get Default Adapter */
	private BluetoothAdapter	_bluetooth				= BluetoothAdapter.getDefaultAdapter();

	/* request BT enable */
	private static final int	REQUEST_ENABLE			= 0x1;
	/* request BT discover */
	private static final int	REQUEST_DISCOVERABLE	= 0x2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent enabler = new Intent(this, ClientSocketActivity.class);
		startActivity(enabler);
	}

	/* Client */
	public void onOpenClientSocketButtonClicked(View view)
	{
		Intent enabler = new Intent(this, ClientSocketActivity.class);
		startActivity(enabler);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
}
