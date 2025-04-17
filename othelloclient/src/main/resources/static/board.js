// Constants
const EMPTY = 0;
const BLACK = 1;
const WHITE = 2;

// Hướng di chuyển trên bàn cờ (8 hướng)
const directions = [
    [-1, -1], [-1, 0], [-1, 1], [0, -1],
    [0, 1], [1, -1], [1, 0], [1, 1]
];

// State variables
let boardState = '';
let currentPlayer = BLACK;
let isThinking = false;

// Elements
const divStatus = document.getElementById('gameStatus');
const boardElement = document.getElementById('othelloBoard');
const blackScoreElement = document.getElementById('blackScore');
const whiteScoreElement = document.getElementById('whiteScore');
const currentPlayerElement = document.getElementById('currentPlayer');

// Initialize board
window.onload = function () {
    // Lấy trạng thái bàn cờ ban đầu từ server
    fetchBoardState();
};

// Lấy trạng thái bàn cờ từ server
async function fetchBoardState() {
    try {
        const response = await fetch('/api/game/board');
        const data = await response.json();

        // Kiểm tra nếu chưa đăng nhập
        if (data.error === 'Unauthorized') {
            window.location.href = '/login';
            return;
        }

        boardState = data.boardState;
        currentPlayer = data.currentPlayer;

        updateScores(data.blackScore, data.whiteScore);
        updateCurrentPlayerDisplay();
        createBoard();

        // Nếu là lượt của bot, yêu cầu bot thực hiện nước đi
        if (currentPlayer === WHITE) {
            makeBotMove();
        }

        // Kiểm tra trò chơi kết thúc
        if (data.gameOver) {
            handleGameOver(data.winner);
        }
    } catch (error) {
        console.error('Error fetching board state:', error);
        divStatus.textContent = 'Lỗi kết nối đến server';
    }
}

// Tạo bàn cờ
function createBoard() {
    boardElement.innerHTML = '';

    for (let row = 0; row < 8; row++) {
        for (let col = 0; col < 8; col++) {
            const cell = document.createElement('div');
            cell.className = 'cell';
            cell.setAttribute('data-row', row);
            cell.setAttribute('data-col', col);

            const piece = parseInt(boardState[row * 8 + col]);

            if (piece === BLACK) {
                const blackDisc = document.createElement('div');
                blackDisc.className = 'black';
                cell.appendChild(blackDisc);
            } else if (piece === WHITE) {
                const whiteDisc = document.createElement('div');
                whiteDisc.className = 'white';
                cell.appendChild(whiteDisc);
            }

            // Xử lý click
            cell.onclick = function () {
                if (currentPlayer === BLACK && !isThinking && !cell.hasChildNodes()) {
                    makeMove(row, col);
                }
            };

            boardElement.appendChild(cell);
        }
    }

    // Đánh dấu các nước đi hợp lệ
    highlightValidMoves();
}

// Đánh dấu nước đi hợp lệ
function highlightValidMoves() {
    // Xóa đánh dấu cũ
    document.querySelectorAll('.cell').forEach(cell => cell.classList.remove('valid-move'));

    // Chỉ đánh dấu nếu là lượt người chơi
    if (currentPlayer === BLACK) {
        for (let row = 0; row < 8; row++) {
            for (let col = 0; col < 8; col++) {
                if (boardState[row * 8 + col] === '0' && isValidMove(row, col, currentPlayer)) {
                    document.querySelector(`[data-row='${row}'][data-col='${col}']`).classList.add('valid-move');
                }
            }
        }
    }
}

// Xác định nước đi có hợp lệ không
function isValidMove(row, col, player) {
    // Nếu ô đã có quân, không hợp lệ
    if (boardState[row * 8 + col] !== '0') {
        return false;
    }

    const opponent = player === BLACK ? WHITE : BLACK;

    for (let [dx, dy] of directions) {
        let r = row + dx;
        let c = col + dy;
        let hasOpponent = false;

        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
            const piece = parseInt(boardState[r * 8 + c]);

            if (piece === 0) {
                break;
            } else if (piece === opponent) {
                hasOpponent = true;
            } else if (piece === player) {
                if (hasOpponent) {
                    return true;
                }
                break;
            }

            r += dx;
            c += dy;
        }
    }

    return false;
}

// Thực hiện nước đi của người chơi
async function makeMove(row, col) {
    if (!isValidMove(row, col, currentPlayer)) {
        return;
    }

    isThinking = true;
    divStatus.textContent = 'Đang xử lý...';

    try {
        const response = await fetch('/api/game/move', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                row: row,
                col: col,
                player: currentPlayer
            })
        });

        const data = await response.json();

        if (data.success) {
            boardState = data.boardState;
            currentPlayer = data.currentPlayer;

            updateScores(data.blackScore, data.whiteScore);
            updateCurrentPlayerDisplay();
            createBoard();

            // Kiểm tra trò chơi kết thúc
            if (data.gameOver) {
                handleGameOver(data.winner);
                isThinking = false;
                return;
            }

            // Nếu là lượt của bot, yêu cầu bot thực hiện nước đi
            if (currentPlayer === WHITE) {
                setTimeout(() => {
                    makeBotMove();
                }, 1000); // Chờ 1 giây để người chơi thấy được nước đi của mình
            } else {
                isThinking = false;
                divStatus.textContent = 'Lượt của bạn';
            }
        } else {
            divStatus.textContent = data.message || 'Nước đi không hợp lệ';
            isThinking = false;
        }
    } catch (error) {
        console.error('Error making move:', error);
        divStatus.textContent = 'Lỗi kết nối đến server';
        isThinking = false;
    }
}

// Yêu cầu bot thực hiện nước đi
async function makeBotMove() {
    isThinking = true;
    divStatus.textContent = 'Bot đang suy nghĩ...';

    try {
        const response = await fetch('/api/game/bot-move');
        const data = await response.json();

        boardState = data.boardState;
        currentPlayer = data.currentPlayer;

        updateScores(data.blackScore, data.whiteScore);
        updateCurrentPlayerDisplay();
        createBoard();

        // Kiểm tra trò chơi kết thúc
        if (data.gameOver) {
            handleGameOver(data.winner);
        } else {
            divStatus.textContent = 'Lượt của bạn';
        }
    } catch (error) {
        console.error('Error making bot move:', error);
        divStatus.textContent = 'Lỗi kết nối đến server';
    }

    isThinking = false;
}

// Cập nhật điểm số
function updateScores(blackScore, whiteScore) {
    blackScoreElement.innerHTML = `Black: <span>${blackScore}</span>`;
    whiteScoreElement.innerHTML = `White: <span>${whiteScore}</span>`;
}

// Cập nhật hiển thị người chơi hiện tại
function updateCurrentPlayerDisplay() {
    currentPlayerElement.textContent = currentPlayer === BLACK ? 'Black (You)' : 'White (Bot)';
}

// Reset game
function resetGame() {
    window.location.href = '/reset-game';
}

// Xử lý khi trò chơi kết thúc
function handleGameOver(winner) {
    if (winner === BLACK) {
        divStatus.textContent = 'Trò chơi kết thúc! Bạn đã thắng!';
    } else if (winner === WHITE) {
        divStatus.textContent = 'Trò chơi kết thúc! Bot đã thắng!';
    } else {
        divStatus.textContent = 'Trò chơi kết thúc! Hòa!';
    }

    // Lưu kết quả ván đấu
    saveGameResult();
}

// Lưu kết quả ván đấu vào database
async function saveGameResult() {
    try {
        const response = await fetch('/save-game-result', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();

        if (data.saved) {
            console.log('Game result saved successfully');
            if (data.playerWon) {
                divStatus.textContent += ' Kết quả đã được lưu vào hồ sơ của bạn.';
            } else {
                divStatus.textContent += ' Kết quả đã được ghi nhận.';
            }
        } else {
            console.error('Failed to save game result');
            divStatus.textContent += ' ' + data.message;
        }
    } catch (error) {
        console.error('Error saving game result:', error);
        divStatus.textContent += ' Lỗi khi lưu kết quả.';
    }
}