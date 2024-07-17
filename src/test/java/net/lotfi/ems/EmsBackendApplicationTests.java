package net.lotfi.ems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
// TODO parametrize sql statement if possible ==> benifit ==> no need to create sql script files
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmsBackendApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	private TestInfo testInfo;

	@BeforeEach
	void init(TestInfo testInfo) {
		this.testInfo = testInfo;
		System.out.println("displayName = " + testInfo.getDisplayName());
	}

	@Test
	@SqlGroup({
			@Sql("/create-employee-1.sql"),
	})
	void getEmployees() throws Exception {
		// Act
		ResultActions result = mockMvc.perform(get("/api/employees"));

		// Assert
		result.andDo(print()).andExpect(status().isOk())
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
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
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
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
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
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
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
		// Assert
		// TODO assert json result content (err msg..etc)
		createLeaveTest(leaveJson).andDo(print()).andExpect(status().is(400));

		// Overlapping leave 2 : exacte leave interval
		leaveJson = "{\"startDate\":\"2024-08-01\",\"endDate\":\"2024-08-10\"}";
		// Assert
		createLeaveTest(leaveJson).andDo(print()).andExpect(status().is(400));

		// Overlapping leave 3 : endDate overlaps
		leaveJson = "{\"startDate\":\"2024-07-30\",\"endDate\":\"2024-08-02\"}";
		// Assert
		createLeaveTest(leaveJson).andDo(print()).andExpect(status().is(400));

		// Overlapping leave 4 : startDate overlaps
		leaveJson = "{\"startDate\":\"2024-08-09\",\"endDate\":\"2024-08-12\"}";
		// Assert
		createLeaveTest(leaveJson).andDo(print()).andExpect(status().is(400));
	}


	ResultActions createLeaveTest(String leaveJson) throws Exception {
		return mockMvc.perform(post("/api/employees/1/leaves")
				.contentType(MediaType.APPLICATION_JSON)
				.content(leaveJson));
	}


}
