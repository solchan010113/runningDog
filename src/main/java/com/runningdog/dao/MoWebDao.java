package com.runningdog.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.runningdog.vo.MoDogVo;
import com.runningdog.vo.MoTrailVo;
import com.runningdog.vo.MoWalkLogVo;
import com.runningdog.vo.UseTrailVo;

@Repository
public class MoWebDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// (1) 산책로 불러오기
	public List<UseTrailVo> trailSelect(){
		System.out.println("@Dao");		
		return sqlSession.selectList("walkLog.mapTrailSelect");
	}	
	
	// (2) 강아지정보 가져오기
	public List<MoDogVo> dogSelect(int userNo){
		System.out.println("다오 강아지가져오기");	
		System.out.println("다오 아이디 "+userNo);	
		List<MoDogVo> dogList = sqlSession.selectList("walkLog.dogSelect", userNo);
		System.out.println(dogList);	
		return dogList;
	}
	
	// (3) 산책기록하기
	public void walkLogInsert(MoWalkLogVo moWalkLogVo){
		System.out.println("다오 산책기록하기");
		moWalkLogVo.setLocationNo(1174010900); // 동네번호
		moWalkLogVo.setMeetingNo(0); // 모임번호
		moWalkLogVo.setSecurity("공개"); // 공개유무
		moWalkLogVo.setStatus('T');		
			
		System.out.println("셀렉트키 " + moWalkLogVo);				
		sqlSession.insert("walkLog.walkLogInsert",moWalkLogVo);	
		System.out.println("셀렉트키 " + moWalkLogVo);
		
		// moWalkLogVo의 walkLogNo를 통해서 산책한강아지 리스트저장,좌표 리스트저장,이미지 리스트저장
		
		
	}
	
	// (0) 유사한 산책로 불러오기 (현재는 더미데이터 3개 불러오기)
	public List<MoTrailVo> trailSelect(int locationNo){
		System.out.println("다오 산책로 3개 불러오기");			
		return sqlSession.selectList("walkLog.trailSelect",locationNo);			
	}
	

}
