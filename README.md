# SingleDiaryApp

## 개요
- 일상에서 일어났던 일들을 간단히 앱에 저장해주며 이를 수시로 확인 가능하고 통계를 통해 자신의 과거 상태를 확인할 수 있다.

## 구성
### MainActivity.java
- 상단 바 및 하단 탭으로 구성, 중간은 3개의 Fragment를 계속 replace하는 방식.

### ContentFragment.java
- DB에 저장된 컨텐츠를 가져와서 띄워보여주는 프래그먼트
  - 상단 '내용/사진' 버튼을 통해 내용 위주의 CardView 및 사진 위주의 CardView 스위칭 가능
  - '오늘 작성' 버튼을 통해 작성 창으로 이동 가능

### CreateFragment.java
- 자신의 일상을 기록해 DB에 저장할 수 있는 프래그먼트
  - 우측 상단은 FusedLocationProviderClient로 구글에서 위치 데이터를 받아 이를 자신의 현재 주소로 자동 변환시켜서 보여줌
  - 좌측 상단은 날씨 API를 통해 자신의 위치에서 날씨가 어떻게 되는지 확인해서 이와 관련된 이미지를 띄워줌
  - 이미지 클릭을 통해 사진 앨범에서 자신이 원하는 위치를 저장 가능
  - SeekBar를 통해 자신의 기분을 저장 가능
  - 저장 버튼: DB에 저장, 삭제 버튼: 지금의 정보를 모두 초기화, 닫기 버튼: 화면 밖으로 나가기

### StatisticFragment.java
- DB에 저장되어 있는 컨텐츠를 분석해서 전체적인 통계를 나타내 보여주는 프래그먼트 
  - 기분별 비율: DB내 전체 데이터에서 기분별 비율이 어떻게 되는지 Pie Chart를 통해 보여줌
  - 요일별 기분: 월요일부터 일요일까지 나눠서 기분별 비율이 어떻게 되는지 Bar Chart를 통해 보여줌
  - 기분 변화: 최근 5개의 데이터로부터 기분이 어떻게 변화해갔는지 Line Chart를 통해 보여줌
  
  ## 사용한 오픈소스
  - 안드로이드 차트: https://github.com/PhilJay/MPAndroidChart
  - 권한 물어보기: https://github.com/pedroSG94/AutoPermissions
