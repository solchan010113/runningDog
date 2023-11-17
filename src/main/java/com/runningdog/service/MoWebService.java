package com.runningdog.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.runningdog.dao.MoWebDao;
import com.runningdog.vo.CoordsVo;
import com.runningdog.vo.MoDogVo;
import com.runningdog.vo.MoTrailVo;
import com.runningdog.vo.MoWalkLogVo;
import com.runningdog.vo.MoWalkedDogVo;
import com.runningdog.vo.UseTrailVo;
import com.runningdog.vo.WalkedDogVo;
import com.runningdog.vo.XYVo;

@Service	
public class MoWebService {
	
	@Autowired
	private MoWebDao moWebDao;
	
	// (1) 산책로 불러오기
	public List<UseTrailVo> trailSelect(){
		System.out.println("@Service");				
		return moWebDao.trailSelect();
	}
	
	// (2) 강아지정보 가져오기
	public List<MoDogVo> dogSelect(int userNo){
		System.out.println("서비스 강아지가져오기");				
		return moWebDao.dogSelect(userNo);
	}
	
	// (3) 산책기록하기
	public void walkLogInsert(MoWalkLogVo moWalkLogVo){
		System.out.println("서비스 산책기록하기");
		//산책기록
		moWebDao.walkLogInsert(moWalkLogVo);
		
		System.out.println("셀렉트키가 포함된 산책기록 : "+moWalkLogVo);	
		System.out.println("셀렉트키 : "+ moWalkLogVo.getWalkLogNo());		
		int walkLogNo = moWalkLogVo.getWalkLogNo();
		
		// 산책한 강아지 리스트 저장 ------------------------------------------------------
		System.out.println("강아지리스트 : "+moWalkLogVo.getDogNoList());
		for (Integer dogNo : moWalkLogVo.getDogNoList()) {
			MoWalkedDogVo walkedDogVo = new MoWalkedDogVo();
			walkedDogVo.setDogNo(dogNo); // 산책한 강아지번호
			walkedDogVo.setWalkLogNo(walkLogNo); // 셀렉트키
            System.out.println("개번호확인 : "+ walkedDogVo);	
            // 한땀한땀 뜯어낸 강아지번호값을 DB로 보내서 저장하기 
            moWebDao.dogListInsert(walkedDogVo);
        }
		
		// 좌표리스트 넣어서 저장 ------------------------------------------------------
		System.out.println("좌표리스트 : "+moWalkLogVo.getPolylinePath());	
        // XYVo 리스트를 순회하면서 CoordsVo로 변환 후 리스트에 추가
		// coordOrder 변수를 1부터 시작
        int coordOrder = 1;
        
        for (XYVo xyVo : moWalkLogVo.getPolylinePath()) {
            CoordsVo coordsVo = new CoordsVo();
            coordsVo.setUseNo(walkLogNo); // 셀렉트키
            coordsVo.setLat(xyVo.getY());  // XYVo의 Y값을 CoordsVo의 lat으로 설정
            coordsVo.setLng(xyVo.getX());  // XYVo의 X값을 CoordsVo의 lng으로 설정
            coordsVo.setCoordOrder(coordOrder);
            // 리스트에 추가
            // coordOrder를 1씩 증가
            coordOrder++;
            System.out.println("좌표확인 : "+ coordsVo);	
            // 한땀한땀 뜯어낸 좌표값을 DB로 보내서 저장하기 
            moWebDao.coordsListInsert(coordsVo);
        }       
		
		//캡쳐이미지
        
		
		//첨부파일처리X
		
	}
	
	// (6) 캡쳐용 좌표값 불러오기
	public List<CoordsVo> mapSelect(int walkLogNo){
		System.out.println("서비스 캡쳐용 좌표값 불러오기");		
		return moWebDao.mapSelect(walkLogNo);		
	}
	
	// (0) 유사한 산책로 불러오기 (현재는 더미데이터 3개 불러오기)
	public List<MoTrailVo> trailSelect(int locationNo){
		System.out.println("서비스 산책로 3개 불러오기");			
		return moWebDao.trailSelect(locationNo);					
	}
	

}
