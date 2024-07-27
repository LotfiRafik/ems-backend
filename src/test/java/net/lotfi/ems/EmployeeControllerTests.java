package net.lotfi.ems;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

import java.time.LocalDate;

// TODO parametrize sql statement if possible ==> benefit ==> no need to create sql script files
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeControllerTests {
	@Autowired
	private MockMvc mockMvc;
	private TestInfo testInfo;

	@BeforeEach
	void init(TestInfo testInfo) {
		this.testInfo = testInfo;
		System.out.println("displayName = " + testInfo.getDisplayName());
	}

	String login() throws Exception{
		String loginBody = "{\"username\":\"john@samir.com\",\"password\":\"test\"}";
		ResultActions result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginBody));

		// Assert
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		String response = result.andReturn().getResponse().getContentAsString();
		String jwtToken = JsonPath.parse(response).read("$.token");
		return jwtToken;
	}


	@Test
	@SqlGroup({
			@Sql("/create-employee-1.sql"),
	})
	void getEmployees() throws Exception {
		// Login required before act
		String jwtToken = login();
		// Act
		ResultActions result = mockMvc.perform(get("/api/employees").
				header("Authorization", "Bearer " + jwtToken));
		// Assert
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	@SqlGroup({
			@Sql("/create-leave-emp-1.sql"),
	})
	void createValideLeave() throws Exception {
		LocalDate currentDate = LocalDate.now();
		LocalDate startDate = currentDate.plusDays(1);
		LocalDate endDate = currentDate.plusDays(4);
		String leaveJson = "{" +
								"\"startDate\" : \"" + startDate.toString() + "\"," +
								"\"endDate\" : \"" + endDate.toString() + "\"" +
							"}";

		// Login required before act
		String jwtToken = login();
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(leaveJson));

		// Assert
		result.andExpect(status().isCreated());
	}

	@Test
	@SqlGroup({
			@Sql("/create-employee-1.sql"),
	})
	void createLeaveWithIncoherentDates() throws Exception {
		/*
			issue : startDate > endDate
		 */
		String leaveJson = "{\"startDate\":\"2024-08-29\",\"endDate\":\"2024-07-29\"}";
		// Login required before act
		String jwtToken = login();
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(leaveJson));

		// Assert
		result.andDo(print()).andExpect(status().is(400));
	}

	@Test
	@SqlGroup({
			@Sql("/create-employee-1.sql"),
	})
	void createLeaveExceedAvailableDays() throws Exception {
		/*
			test : Exceeded available leave days
		 */
		String leaveJson = "{\"startDate\":\"2024-09-01\",\"endDate\":\"2024-09-30\"}";
		// Login required before act
		String jwtToken = login();
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(leaveJson));

		// Assert
		result.andExpect(status().is(400));
	}

	@Test
	@SqlGroup({
			@Sql("/create-leave-emp-1.sql"),
	})
	void createOverlappingLeave(TestInfo testInfo) throws Exception {
		/*
			test : Overlapping leaves
		 */

		// Overlapping leave 1 : fully overlapped
		String leaveJson = "{\"startDate\":\"2024-08-02\",\"endDate\":\"2024-08-09\"}";
		// Login required before act
		String jwtToken = login();

		// Assert
		// TODO assert json result content (err msg..etc)
		createLeaveTest(leaveJson, jwtToken).andDo(print()).andExpect(status().is(400));

		// Overlapping leave 2 : exacte leave interval
		leaveJson = "{\"startDate\":\"2024-08-01\",\"endDate\":\"2024-08-10\"}";
		// Assert
		createLeaveTest(leaveJson, jwtToken).andDo(print()).andExpect(status().is(400));

		// Overlapping leave 3 : endDate overlaps
		leaveJson = "{\"startDate\":\"2024-07-30\",\"endDate\":\"2024-08-02\"}";
		// Assert
		createLeaveTest(leaveJson, jwtToken).andDo(print()).andExpect(status().is(400));

		// Overlapping leave 4 : startDate overlaps
		leaveJson = "{\"startDate\":\"2024-08-09\",\"endDate\":\"2024-08-12\"}";
		// Assert
		createLeaveTest(leaveJson, jwtToken).andDo(print()).andExpect(status().is(400));
	}


	ResultActions createLeaveTest(String leaveJson, String jwtToken) throws Exception {
		return mockMvc.perform(post("/api/employees/1/leaves")
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(leaveJson));
	}



}
