import requests

url = "http://localhost:8082/api"
signup = f"{url}/users/signup"
login_url = f"{url}/users/login"
approve_url = f"{url}/users/change-company-status"
order_url = f"{url}/deliveries/company"
rider_url = f"{url}/deliveries/rider/accept"

companies = [
    {
        "username": "Zap",
        "password": "zapogus123",
        "role": "COMPANY",
        "name": "Zap"
    },
    {
        "username": "Zapebook",
        "password": "zapogus123",
        "role": "COMPANY",
        "name": "Zapebook"
    },
    {
        "username": "Zapzap",
        "password": "zapogus123",
        "role": "COMPANY",
        "name": "Zapzap"
    },
    {
        "username": "Zapgram",
        "password": "zapogus123",
        "role": "COMPANY",
        "name": "Zapgram"
    },
    {
        "username": "Zaper",
        "password": "zapogus123",
        "role": "COMPANY",
        "name": "Zaper"
    },
]

manager = {
    "username": "caio_costela",
    "password": "amogus123",
    "role": "MANAGER",
    "name": "Caio Costela"
}

approved_companies = [
    {
        "id": 1,
        "companyStatus": "APPROVED"
    },
    {
        "id": 3,
        "companyStatus": "APPROVED"
    }
]

riders = [
    {
        "username": "not_caio_costela",
        "password": "amogus123",
        "role": "RIDER",
        "name": "Caio Costela"
    },
    {
        "username": "licius_vinicious",
        "password": "amogus123",
        "role": "RIDER",
        "name": "Lucius Vinicius"
    },
    {
        "username": "tom_carvalo",
        "password": "amogus123",
        "role": "RIDER",
        "name": "Tom Carvalo"
    },
    {
        "username": "isaeb",
        "password": "amogus123",
        "role": "RIDER",
        "name": "Isaeb Rosnario"
    }
]

orders = [
    {
        "buyer": "Carlos Costa",
        "destination": "Rua Sao Sebastiao 124",
        "notes": "A curious order",
        "origin": "Zap Store - Aveiro, Portugal"
    },
    {
        "buyer": "Ilidio Castro Oliveira",
        "destination": "Rua Sao Sebastiao 123",
        "notes": "A map order",
        "origin": "Zap Store - Aveiro, Portugal"
    },
    {
        "buyer": "Joao Silva Pereira",
        "destination": "Rua da Alegria 24",
        "notes": "",
        "origin": "Zapzap Store - Aveiro, Portugal"
    },
]


def main():
    for company in companies:
        requests.post(signup, json = company)
    
    for rider in riders:
        requests.post(signup, json = rider)

    requests.post(signup, json = manager)

    # Login manager

    logi = requests.post(login_url, json = manager)
    manager_token = logi.json()['token']['token']
    print(manager_token)

    # Approve 

    headers = {
        "Authorization": f"Bearer {manager_token}"
    }

    for company in approved_companies:
        x = requests.post(approve_url, json = company, headers = headers)

    # Create orders

    logi = requests.post(login_url, json = companies[0])
    company_token = logi.json()['token']['token']
    print(company_token)

    headers = {
        "Authorization": f"Bearer {company_token}"
    }

    for order in orders:
        x = requests.post(order_url, json = order, headers = headers)

    # Riders accept
    i = 11

    for rider in riders:
        logi = requests.post(login_url, json = companies[0])
        rider_token = logi.json()['token']['token']
        headers = {
            "Authorization": f"Bearer {rider_token}"
        }
        content = {
            "orderId": i
        }
        x = requests.post(rider_url, data = i, headers = headers)
        print(f"{i}: {x.text}")
        i += 1


if __name__ == "__main__":
    main()
