package com.example.pomodoro;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientSocketActivity  extends Activity
{
	private static final String TAG = ClientSocketActivity.class.getSimpleName();
	private static final int REQUEST_DISCOVERY = 0x1;
	private Handler _handler = new Handler();
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	
	private BluetoothSocket socket = null;
	private String str;
	private OutputStream outputStream;
	private InputStream inputStream;
	private StringBuffer sbu;
	
	private TextView sTextView;
	private EditText sEditText;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_socket);
		
		Button button1 = (Button)findViewById(R.id.button1);
		Button button2 = (Button)findViewById(R.id.button2);
		Button button3 = (Button)findViewById(R.id.button3);
		Button buttonSend = (Button)findViewById(R.id.buttonSend);
		sTextView = (TextView)findViewById(R.id.sTextView);
		sEditText = (EditText)findViewById(R.id.sEditText);
		
		if (!_bluetooth.isEnabled()) {
			finish();
			return;
		}
		Intent intent = new Intent(this, DiscoveryActivity.class);
		
		/* Prompted to select a server to connect */
		Toast.makeText(this, "연결할 BlueTooth 장비를 검색합니다.", Toast.LENGTH_SHORT).show();
		
		/* Select device for list */
		startActivityForResult(intent, REQUEST_DISCOVERY);
		
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(str != null)
				{
					sTextView.setText(str + "--> " + sEditText.getText());
					str += ("--> " + sEditText.getText().toString());
				}
				else
				{
					sTextView.setText("--> " + sEditText.getText());
					str = "--> " + sEditText.getText().toString();
				}
				str += '\n';
				
				String tmpStr=sEditText.getText().toString();
				byte bytes[] = tmpStr.getBytes();
				try {
					outputStream.write(bytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					sendToBT("25");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					sendToBT("5");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					sendToBT("30");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void sendToBT(String strTime) throws IOException {
		
		if(str != null)
		{
			Log.d("Pomodoro", ">>second");
			sTextView.setText(str + "--> " + strTime);
			str += ("--> " + strTime);
		}
		else
		{
			Log.d("Pomodoro", ">>frist");
			sTextView.setText("--> " + strTime);
			str = "--> " + strTime;
		}
		str += '\n';
		
		String tmpStr = strTime;
		byte bytes[] = tmpStr.getBytes();
		outputStream.write(bytes);
	}
	
	/* after select, connect to device */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_DISCOVERY) {
			finish();
			return;
		}
		if (resultCode != RESULT_OK) {
			finish();
			return;
		}
		final BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		new Thread() {
			public void run() {
				connect(device);
			};
		}.start();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				Log.e("Pomodoro", ">>", e);
			}
		}
	}
	
	protected void connect(BluetoothDevice device) {
		try {
			//Create a Socket connection: need the server's UUID number of registered
			socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			
			socket.connect();
			Log.d("Pomodoro", ">>Client connectted");
		
			inputStream = socket.getInputStream();														
			outputStream = socket.getOutputStream();
			int read = -1;
			final byte[] bytes = new byte[2048];
			for (; (read = inputStream.read(bytes)) > -1;) {
				final int count = read;
				_handler.post(new Runnable() {
					public void run() {
						StringBuilder b = new StringBuilder();
						for (int i = 0; i < count; ++i) {
							String s = Integer.toString(bytes[i]);
							b.append(s);
							b.append(",");
						}
						String s = b.toString();
						String[] chars = s.split(",");
						sbu = new StringBuffer();  
						 for (int i = 0; i < chars.length; i++) {  
						        sbu.append((char) Integer.parseInt(chars[i]));  
						    }
						Log.d("Pomodoro", ">>inputStream");
						if(str != null)
						{		
							sTextView.setText(str + "<-- " + sbu);
							str += ("<-- " + sbu.toString());
						}
						else
						{
							sTextView.setText("<-- " + sbu);
							str = "<-- " + sbu.toString();
						}
						str += '\n';
					}
				}); 
			}
			
		} catch (IOException e) {
			Log.e("Pomodoro", ">>", e);
			finish();
			return ;
		} finally {
			if (socket != null) {
				try {
					Log.d("Pomodoro", ">>Client Socket Close");
					socket.close();	
					finish();
					return ;
				} catch (IOException e) {
					Log.e("Pomodoro", ">>", e);
				}
			}
		}
	}
}


