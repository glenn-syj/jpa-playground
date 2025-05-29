const API_BASE_URL = "http://localhost:8080/api/v1/members";

async function createMember() {
  const nameInput = document.getElementById("memberName");
  const typeSelect = document.getElementById("memberType");
  const name = nameInput.value.trim();
  const type = typeSelect.value;

  if (!name) {
    showResult("Please enter a member name");
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/tsid`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ name: name, type: type }), // type 추가
    });

    const data = await response.json();
    showResult("Member Created:", data);
    nameInput.value = "";
  } catch (error) {
    showResult("Error creating member:", error);
  }
}

async function getMember() {
  const tsid = document.getElementById("tsidInput").value.trim();

  if (!tsid) {
    showResult("Please enter a TSID");
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/tsid/${tsid}`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    showResult("Member Found:", data);
  } catch (error) {
    showResult("Error fetching member:", error);
  }
}

function showResult(message, data = null) {
  const resultElement = document.getElementById("result");
  resultElement.textContent =
    message + "\n" + (data ? JSON.stringify(data, null, 2) : "");
}
