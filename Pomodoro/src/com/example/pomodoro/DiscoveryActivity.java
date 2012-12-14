package com.example.pomodoro;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DiscoveryActivity  extends ListActivity
{
	private Handler _handler = new Handler();
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();	/* Default Adapter */
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();	/* Storage the BT devices */
	private volatile boolean _discoveryFinished;	/* Discovery is Finished */
	
	private Runnable _discoveryWorkder = new Runnable() {
		public void run() 
		{
			/* Start search device */
			_bluetooth.startDiscovery();
			Log.d("Pomodoro", ">>Starting Discovery");
			for (;;) 
			{
				if (_discoveryFinished) 
				{
					Log.d("Pomodoro", ">>Finished");
					break;
				}
				try 
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e){}
			}
		}
	};
	
	/**
	 * Receiver
	 * When the discovery finished be called.
	 */
	private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			/* get the search results */
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			_devices.add(device);
			/* show the devices list */
			showDevices();
		}
	};
	
	private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)  
		{
			/* unRegister Receiver */
			Log.d("Pomodoro", ">>unregisterReceiver");
			unregisterReceiver(_foundReceiver);
			unregisterReceiver(this);
			_discoveryFinished = true;
		}
	};
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.discovery);
		
		/* BT isEnable */
		if (!_bluetooth.isEnabled())
		{
			Log.w("Pomodoro", ">>BTBee is disable!");
			finish();
			return;
		}
		/* Register Receiver*/
		IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(_discoveryReceiver, discoveryFilter);
		
		//어플리케이션은 각 발견된 디바이스에 대한 정보를 받기 위해  ACTION_FOUND 인텐트를 위한 BroadcastReceiver를 등록해야만 한다. 
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(_foundReceiver, foundFilter);
		
		/* "Scanning..." 다이얼로그 출력한다. */
		SamplesUtils.indeterminate(DiscoveryActivity.this, _handler, "Scanning...", _discoveryWorkder, new OnDismissListener() {
			public void onDismiss(DialogInterface dialog)
			{

				for (; _bluetooth.isDiscovering();)
				{

					_bluetooth.cancelDiscovery();
				}

				_discoveryFinished = true;
			}
		}, true);
	}

	/* Show devices list */
	protected void showDevices()
	{
		List<String> list = new ArrayList<String>();
		if(_devices.size() > 0)
		{	
			for (int i = 0, size = _devices.size(); i < size; ++i)
			{
				StringBuilder b = new StringBuilder();
				BluetoothDevice d = _devices.get(i);

				//디바이스 리스트에 디바이스를 추가한다. 
				b.append(d.getAddress());
				b.append('\n');
				b.append(d.getName());
				String s = b.toString();
				list.add(s);
				
				//MPUCAFE라는 이름의 deviceName 찾으면 바로 탐색을 종료한다.
				if(d.getName().toString().equals("MPUCAFE")){
					_bluetooth.cancelDiscovery();
					break;
				}
				
			}
		}
		else
			list.add("There isn't bluetooth devices");
		Log.d("Pomodoro", ">>showDevices");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		_handler.post(new Runnable() {
			public void run()
			{
				setListAdapter(adapter);
			}
		});
	}
	
	/* Select device */
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Log.d("Pomodoro", ">>Click device");
		Intent result = new Intent();
		result.putExtra(BluetoothDevice.EXTRA_DEVICE, _devices.get(position));
		setResult(RESULT_OK, result);
		finish();
	}
}

