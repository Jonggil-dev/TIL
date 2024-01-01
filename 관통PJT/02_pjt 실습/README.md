#### numpy, pandas, matplotlib

    1. CSV 파일을 DataFrame으로 파싱 하는 방법  

        (1) numpy로 csv file open 후 numpy 배열로 변경   
            → 그 다음 pandas.DataFrame() 메서드 이용  

        (2) 처음부터 바로 Pandas.read_csv('파일명') 으로 데이터 프레임 생성  
            →  특정 필드(항목) 만 읽어오고 싶으면 usecols 매개변수 사용  
            →  Pandas.read_csv('파일명'. **usecols = ['필드명1','필드명2', ...**)  

2. 데이터 프레임에서는 2차원 배열이기 때문에 인덱스와 컬럼을 가짐  

        (1) 인덱스 :  행(row)을 식별하기 위한 레이블 (인덱스를 통해 행 전체 값을 추출 가능)  

        (2) 컬럼 : 각 열의 이름을 지정하기 위한 레이블 (각 열에 대한 대표 이름? 같은거)  

   3. dtype이 period인 경우 함수를 작성할 때 float형식으로 형변환이 되지 않아서 사용할 수없다. dt.strftime() 메서드를 통해 형변환을 해주어야 한다  


