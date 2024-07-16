package net.lotfi.ems;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
// TODO check how to create brand new database as spring does for each test
// TODO after each test delete all data, leave schema
// TODO , all tests need same database schema (tables, contraints..etc) , but each test needs a fresh database so it wouldnt be dependent on other tests

@SpringBootTest
@AutoConfigureMockMvc
class EmsBackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Disabled
	@SqlGroup({
			@Sql("/create-employee-1.sql"),
			@Sql("/reverse-create-employee-1.sql"),
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
			@Sql("/create-employee-1.sql"),
			@Sql("/create-leave-emp-1.sql"),
			@Sql("/create-leave-emp-1.sql"),
	})
	void createValideLeave() throws Exception {
		LocalDate currentDate = LocalDate.now();
		LocalDate startDate = currentDate.plusDays(1);
		LocalDate endDate = currentDate.plusDays(4);

		String leaveJson = "{" +
								"\"startDate\" : " + startDate.toString() + "," +
								"\"endDate\" : " + endDate.toString() +
							"}";
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
				.contentType(MediaType.APPLICATION_JSON)
				.content(leaveJson));

		// Assert
		result.andExpect(status().isCreated());
	}

	@Test
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
		result.andExpect(status().is(400));
	}

	@Test
	void createLeaveExceedAvailableDays() throws Exception {
		/*
			issue : exceeded available leave days
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
	@Disabled
	void createOverlappingLeave() throws Exception {
		/*
			issue : Overlapping leave
		 */
		String leaveJson = "{\"startDate\":\"2024-09-01\",\"endDate\":\"2024-09-30\"}";
		// Act
		ResultActions result = mockMvc.perform(post("/api/employees/1/leaves")
				.contentType(MediaType.APPLICATION_JSON)
				.content(leaveJson));

		// Assert
		result.andExpect(status().is(400));
	}


}
