# Git

## 1. 요약

#### (1) 공간

        -  Working Directory = 내 컴퓨터로 생각

        - Staging Area= 임시 공간으로 생각

        - Repository= 외부 컴퓨터로 생각

#### (2) Add, Commit, Push

        - add : 내 컴퓨터에서 임시공간으로 추가한다

        - commit : (추가한 것에다가 변경사항 또는 메모를 추가한다) - 쪽지

        - push : 임시공간 → 외부컴퓨터

## 2. Git 업로드 방법

1. 폴더로 이동(cd)

2. git init

3. git add 파일명

4. git config --global user.email "[whdrlf3928@naver.com](mailto:whdrlf3928@naver.com)" 

5. git config --global [user.name](http://user.name) "Jeongjonggil"

6. git commit -m "메모"

7. git remote add 주소명칭 링크
   (1) 주소명칭 : origin, rmorigin 등 주소 
   (2) 링크 : https://github.com/JeongJonggil/test.git)

8. git push -u 주소명 branch

※ 레포지토리 주소 등록하는 거는 git init ~ git push 사이에 실행하면 됨 (즉, git push 전에만 실행되면 됨)

## 3. Git 문법

- 상태 확인 : git status, git log

- 설정된 주소 확인 : git remote -v

- 레포지토리 그대로 다운로드 : git clone [주소명(or 주소)] [브랜치이름] <br>
   **>> 클론으로 땡겨오면 자동으로 주소가 origin으로 등록돼서 따로 주소명 입력 안해도 됨**

- 레포지토리 상에서 바뀐 내용만 가져오기 (원본 파일은 PC에 보유한 상태) : git pull [주소명(or 주소)] [브랜치이름]
