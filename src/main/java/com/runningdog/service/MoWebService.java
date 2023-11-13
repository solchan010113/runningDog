package com.runningdog.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.runningdog.dao.MoWebDao;
import com.runningdog.vo.MoDogVo;
import com.runningdog.vo.UseTrailVo;

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
	public List<MoDogVo> dogSelect(String userId){
		System.out.println("서비스 강아지가져오기");				
		return moWebDao.dogSelect(userId);
	}
	

}
