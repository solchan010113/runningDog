package com.runningdog.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.runningdog.vo.MoDogVo;
import com.runningdog.vo.UseTrailVo;

@Repository
public class MoWebDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// (1) 산책로 불러오기
	public List<UseTrailVo> trailSelect(){
		System.out.println("@Dao");		
		return sqlSession.selectList("walkLog.trailSelect");
	}	
	
	// (2) 강아지정보 가져오기
	public List<MoDogVo> dogSelect(String userId){
		System.out.println("다오 강아지가져오기");	
		System.out.println("다오 아이디 "+userId);	
		List<MoDogVo> dogList = sqlSession.selectList("walkLog.dogSelect", userId);
		System.out.println(dogList);	
		return dogList;
	}
	

}
