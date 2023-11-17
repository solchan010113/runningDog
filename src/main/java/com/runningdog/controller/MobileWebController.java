package com.runningdog.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runningdog.service.MoWebService;
import com.runningdog.service.UserService;
import com.runningdog.vo.CoordsVo;
import com.runningdog.vo.LinePathVo;
import com.runningdog.vo.MoDogVo;
import com.runningdog.vo.MoImagesVo;
import com.runningdog.vo.MoTrailVo;
import com.runningdog.vo.MoWalkLogVo;
import com.runningdog.vo.UserVo;

@Controller
@RequestMapping("/m")
public class MobileWebController {

	@Autowired
	private MoWebService moWebService;
	@Autowired
	private UserService userService;

	// 로그인폼
	@RequestMapping("/loginForm")
	public String loginForm() {
		System.out.println("모바일 로그인폼");
		return "mobileWeb/loginForm";
	}

	// 로그인폼
	@RequestMapping("/login")
	public String login(@ModelAttribute UserVo userVo, HttpSession session) {
		System.out.println("모바일 로그인");
		// 로그인 후 세션에 삽입
		UserVo authUser = userService.selectOneUser(userVo);
		if (authUser != null) {
			session.setAttribute("authUser", authUser);
			return "redirect:map";
		} else {
			return "redirect:loginForm";
		}
	}

	// 로그인 모바일메인화면 맵실행
	@RequestMapping(value = "/map", method = { RequestMethod.GET, RequestMethod.POST })
	public String map(HttpSession session, Model dogModel) {
		System.out.println("모바일 산책창");
		UserVo authUser = (UserVo) session.getAttribute("authUser");
		// 강아지정보 불러오기
		List<MoDogVo> dogList = moWebService.dogSelect(authUser.getUserNo());

		System.out.println("내 강아지 리스트 " + dogList);

		dogModel.addAttribute("dogList", dogList);

		return "mobileWeb/walkStart";

		// 산책로 정보 불러오기 (이걸 어떻게?) <-- 현재 내 위치를 기준으로
		// List<UseTrailVo> trailList = moWebService.trailSelect();
		// System.out.println(trailList);
		// model.addAttribute("trailList",trailList);

		// 모임정보 (후순위)

	}

	// 로그아웃
	@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
	public String logout(HttpSession session) {
		System.out.println("모바일 로그아웃");
		// 세션의 모든 값을 지움.
		session.invalidate();
		return "redirect:/";
	}

	// 산책기록폼
	@RequestMapping("/wif")
	public String wif(@ModelAttribute MoWalkLogVo moWalkLogVo, Model walkLogModel,
			@RequestParam(name = "line") String lineData, Model lineModel,
			@RequestParam(name = "dogList") String dogList, Model dogModel, Model trailModel)
			throws JsonParseException, JsonMappingException, IOException {

		// 산책기록
		System.out.println("산책기록 " + moWalkLogVo);
		walkLogModel.addAttribute("moWalkLogVo", moWalkLogVo);

		// 산책한강아지 -------------------------------------------------
		// 쉼표(,)를 기준으로 문자열을 분할하여 List로 변환
		List<String> dogNoList = Arrays.asList(dogList.split(","));

		System.out.println("산책한 강아지 번호 " + dogList);

		dogModel.addAttribute("dogNoList", dogList);

		// 산책한경로좌표 ------------------------------------------------
		System.out.println("wif" + lineData);

		// URL 디코딩을 수행하여 JSON 문자열을 원래 형식으로 변환
		String decodedJson = URLDecoder.decode(lineData, StandardCharsets.UTF_8);

		// JSON 문자열을 파싱하여 객체로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		List<LinePathVo> lineList = objectMapper.readValue(decodedJson, new TypeReference<List<LinePathVo>>() {
		});

		// lineData를 사용하여 다른 작업을 수행
		System.out.println("lineList" + lineList);
		lineModel.addAttribute("lineList", objectMapper.writeValueAsString(lineList)); // JSON 형식의 문자열로 변환하여 전달
		System.out.println(objectMapper.writeValueAsString(lineList));

		// 주변산책로 ---------------------------------------------------
		int locationNo = 1174010900; // 동네정보
		List<MoTrailVo> trailList = moWebService.trailSelect(locationNo);
		System.out.println("산책로3개 컨트롤러" + trailList);

		trailModel.addAttribute("trailList", trailList);

		return "mobileWeb/walkEnd"; // wif 페이지로 이동
	}

	// 산책 기록하기
	@ResponseBody
	@RequestMapping("/walkInsert")
	public MoWalkLogVo walkInsert(@RequestBody MoWalkLogVo moWalkLogVo, HttpSession session) {
		System.out.println("산책기록하기 walkInsert");
		// 세션에서 유저정보 가져오기
		UserVo authUser = (UserVo) session.getAttribute("authUser");
		// 유저번호 산책Vo에 세팅
		moWalkLogVo.setUserNo(authUser.getUserNo());
		moWebService.walkLogInsert(moWalkLogVo); // 여기서 셀렉트키 반환
		return moWalkLogVo; // ajax로 재반환
	}

	// 이미지저장기록하기
	@RequestMapping("/imagesInsert")
	public ResponseEntity<String> handleImagesInsert(
	        @RequestParam("walkLogNo") int walkLogNo,
	        @RequestPart("image") MultipartFile image) {
		// System.out.println("walkInsert3");
		// 파일+workLisgNo
		// 파일1개 저장...
		System.out.println("이미지와 함께 저장할 셀렉트키" +walkLogNo);
		
		System.out.println("첨부이미지리스트 " + image);
		// 1117 파일저장완료
		// 이제 이미지들을 DB저장 해주기
		
		 //(2)파일저장(서버쪽 하드디스크에 저장)/////////이거 서비스에서 처리하기///////////////// 
		try { byte[]
			fileData; fileData = image.getBytes();
			 
			OutputStream os = new
			FileOutputStream("C:\\javaStudy\\upload\\"+image.getOriginalFilename()+".png"); BufferedOutputStream
			bos = new BufferedOutputStream(os);
			 
			bos.write(fileData); bos.close();
		 
		} catch (IOException e) { // TODO Auto-generated catch block
		e.printStackTrace(); }
			 
	
		// 처리 결과에 따라 응답을 구성
        String responseMessage = "File uploaded successfully.";
        return ResponseEntity.ok(responseMessage);
	}

//------------------------------------------------------------------------------------------
	// 맵 캡쳐하는 페이지
	@RequestMapping("/walkMap")
	public String walkMap(@RequestParam(name = "walkLogNo") int walkLogNo, Model model) {
		System.out.println("walkMap");

		System.out.println(walkLogNo);

		List<CoordsVo> coordList = moWebService.mapSelect(walkLogNo);

		// CoordsVo를 JSON 문자열로 변환하여 모델에 추가
		StringBuilder jsonBuilder = new StringBuilder("[");
		for (CoordsVo coordVo : coordList) {
			if (jsonBuilder.length() > 1) {
				jsonBuilder.append(",");
			}
			jsonBuilder.append("{\"lat\":").append(coordVo.getLat()).append(",\"lng\":").append(coordVo.getLng())
					.append("}");
		}
		jsonBuilder.append("]");

		System.out.println(jsonBuilder.toString());

		model.addAttribute("lineList", jsonBuilder.toString());

		return "mobileWeb/walkMap";
	}

//------------------------------------------------------------------------------------------	

	// 산책로 알고리즘
	@RequestMapping("/trailMap")
	public String map() {
		System.out.println("/산책로 알고리즘");
		return "global/dogMapExample";
	}

}
