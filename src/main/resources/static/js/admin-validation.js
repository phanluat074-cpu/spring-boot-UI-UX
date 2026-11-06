/**
 * admin-validation.js
 * Client-side validation cho các form admin.
 * Sử dụng Bootstrap 5 validation classes (.is-invalid, .invalid-feedback).
 */
(function () {
    'use strict';

    /* ── Hàm tiện ích ─────────────────────────────────────────── */

    function isBlank(value) {
        return value === null || value === undefined || value.trim() === '';
    }

    function isValidUrl(value) {
        if (isBlank(value)) return false;
        var lower = value.trim().toLowerCase();
        return lower.startsWith('http://') || lower.startsWith('https://');
    }

    /**
     * Hiển thị lỗi validation cho một field.
     * @param {HTMLElement} field - input/select/textarea element
     * @param {string} message - thông báo lỗi
     */
    function showError(field, message) {
        field.classList.add('is-invalid');
        field.classList.remove('is-valid');
        // Tìm hoặc tạo div .invalid-feedback ngay sau field
        var feedback = field.parentElement.querySelector('.invalid-feedback');
        if (!feedback) {
            feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            field.parentElement.appendChild(feedback);
        }
        feedback.textContent = message;
        feedback.style.display = 'block';
    }

    /**
     * Xóa lỗi validation cho một field.
     * @param {HTMLElement} field - input/select/textarea element
     */
    function clearError(field) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        var feedback = field.parentElement.querySelector('.invalid-feedback');
        if (feedback) {
            feedback.textContent = '';
            feedback.style.display = 'none';
        }
    }

    /**
     * Reset tất cả validation state của form.
     * @param {HTMLFormElement} form
     */
    function resetValidation(form) {
        var fields = form.querySelectorAll('.is-invalid, .is-valid');
        fields.forEach(function (f) {
            f.classList.remove('is-invalid', 'is-valid');
        });
        var feedbacks = form.querySelectorAll('.invalid-feedback');
        feedbacks.forEach(function (fb) {
            fb.textContent = '';
            fb.style.display = 'none';
        });
    }

    /* ── Các hàm validate riêng lẻ ───────────────────────────── */

    function validateRequired(field, message) {
        if (isBlank(field.value)) {
            showError(field, message);
            return false;
        }
        clearError(field);
        return true;
    }

    function validateMaxLength(field, maxLen, message) {
        if (!isBlank(field.value) && field.value.trim().length > maxLen) {
            showError(field, message);
            return false;
        }
        clearError(field);
        return true;
    }

    function validateUrl(field, message) {
        if (!isBlank(field.value) && !isValidUrl(field.value)) {
            showError(field, message);
            return false;
        }
        clearError(field);
        return true;
    }

    function validateUrlRequired(field, message) {
        if (isBlank(field.value)) {
            showError(field, 'Trường này không được để trống.');
            return false;
        }
        if (!isValidUrl(field.value)) {
            showError(field, message);
            return false;
        }
        clearError(field);
        return true;
    }

    function validateSelect(field, message) {
        if (isBlank(field.value)) {
            showError(field, message);
            return false;
        }
        clearError(field);
        return true;
    }

    /* ── Validate từng form cụ thể ────────────────────────────── */

    /**
     * Validate một form dựa trên mảng rules.
     * Mỗi rule: { name: 'fieldName', rules: [{ type: 'required'|'maxLength'|'url'|'urlRequired'|'select', message: '...', max?: N }] }
     * @param {HTMLFormElement} form
     * @param {Array} fieldRules
     * @returns {boolean} true nếu hợp lệ
     */
    function validateForm(form, fieldRules) {
        var allValid = true;

        fieldRules.forEach(function (fr) {
            var field = form.querySelector('[name="' + fr.name + '"]');
            if (!field) return;

            // Xử lý từng rule theo thứ tự, dừng ở rule đầu tiên fail
            for (var i = 0; i < fr.rules.length; i++) {
                var rule = fr.rules[i];
                var valid = true;

                switch (rule.type) {
                    case 'required':
                        valid = validateRequired(field, rule.message);
                        break;
                    case 'maxLength':
                        valid = validateMaxLength(field, rule.max, rule.message);
                        break;
                    case 'url':
                        valid = validateUrl(field, rule.message);
                        break;
                    case 'urlRequired':
                        valid = validateUrlRequired(field, rule.message);
                        break;
                    case 'select':
                        valid = validateSelect(field, rule.message);
                        break;
                }

                if (!valid) {
                    allValid = false;
                    break; // dừng ở rule đầu tiên lỗi cho field này
                }
            }
        });

        return allValid;
    }

    /* ── Hàm chính: gắn validation vào form ──────────────────── */

    /**
     * Gắn validation client-side vào form.
     * @param {string} formSelector - CSS selector cho form (ví dụ: 'form[action*="/save"]')
     * @param {Array} fieldRules - mảng rules (xem validateForm)
     */
    function attachValidation(formSelector, fieldRules) {
        var form = document.querySelector(formSelector);
        if (!form) return;

        // Validate khi submit
        form.addEventListener('submit', function (e) {
            var valid = validateForm(form, fieldRules);
            if (!valid) {
                e.preventDefault();
                e.stopPropagation();
                // Scroll đến field lỗi đầu tiên
                var firstInvalid = form.querySelector('.is-invalid');
                if (firstInvalid) {
                    firstInvalid.focus();
                    firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
        });

        // Real-time validation khi blur
        fieldRules.forEach(function (fr) {
            var field = form.querySelector('[name="' + fr.name + '"]');
            if (!field) return;

            field.addEventListener('blur', function () {
                for (var i = 0; i < fr.rules.length; i++) {
                    var rule = fr.rules[i];
                    var valid = true;

                    switch (rule.type) {
                        case 'required':
                            valid = validateRequired(field, rule.message);
                            break;
                        case 'maxLength':
                            valid = validateMaxLength(field, rule.max, rule.message);
                            break;
                        case 'url':
                            valid = validateUrl(field, rule.message);
                            break;
                        case 'urlRequired':
                            valid = validateUrlRequired(field, rule.message);
                            break;
                        case 'select':
                            valid = validateSelect(field, rule.message);
                            break;
                    }

                    if (!valid) break;
                }
            });

            // Xóa lỗi khi user bắt đầu nhập lại
            field.addEventListener('input', function () {
                clearError(field);
            });
        });
    }

    /* ── Expose ra global ─────────────────────────────────────── */

    window.AdminValidation = {
        attachValidation: attachValidation,
        resetValidation: resetValidation
    };
})();
