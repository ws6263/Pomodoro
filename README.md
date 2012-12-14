
  1. 아두이노 보드에 전원 인가(USB) 

    1. 빵판에 첫 번째 LED가 대기 모드일 때 계속해서 점멸하면서 대기 중임을 알려준다.
    2. 블루투스 모듈에 있는 LED또한 대기 중일 때 계속해서 점멸한다. 
  2. 안드로이드 앱 실행 

    1. CientSocketActivity.onCreate()

      1. startActivityForResult(intent, REQUEST_DISCOVERY);
    2. DiscoveryActivity.onCreate()

      1. _bluetooth.isEnabled()
      2. registerReceiver(_discoveryReceiver, discoveryFilter);
      3. registerReceiver(_foundReceiver, foundFilter);
      4. _discoveryWorkder() Thread 실행 

        1. 실행되자마자 주변에 있는 블루투스 기기들을 12초간 검색한다
      5. DiscoveryActivity.showDevices()

        1. 검색되는 장비들을 리스트에 보여준다.
        2. MPUCAFE라는 이름의 블루투스를 발견하면 블루투스 검색을 즉시 멈춘다.
    3. 검색된 디바이스 중 뽀모도로 블루투스에 해당하는 MPUCAFE라는 블루투스를 누른다. 
  3. 블루투스 모듈과 연결진행 후 쓰레드 생성 (CientSocketActivity.connect())

    1. connect를 하면서 소켓을 생성해 블루투스 장비와 연결을 한다. 
    2. 인풋스트림 아웃풋 스트림을 가져온다.
    3. Thread를 생성하여 데이터를 읽어들인다. 
  4. android 에서 버튼을 누르게 되면 sendToBT 발생 

    1. 버튼에 할당된 시간을 소켓을 통해 블루투스 모듈로 전송하게 된다. 
    2. outputStream.write(bytes);
  5. 블루투스 모듈에서 데이터를 받아 해당 행동에 맞는 뽀모도로 시작 
  6. if 빵만 위에 있느 버튼을 누르게 되면 뽀모도로 즉시 취소 

    1. 안드로이드 폰에 진동을 울리면서 취소되었다는 토스트를 띄운다. 
  7. 시간이 완료되었으면 LED 전체를 점멸 한다음에 다시 대기 상태로 돌아간다

    1. 안드로이드 폰에 진동을 울리면서 완료되었다는 토스트를 띄운다. 
Pomodoro
========
