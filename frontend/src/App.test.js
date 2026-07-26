import { render, screen } from '@testing-library/react';
import axios from 'axios';
import App from './App';

jest.mock('axios');

test('renders the authentication form without calling the API', () => {
  render(<App />);
  expect(screen.getByText(/node_authentication/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /connect_to_node/i })).toBeInTheDocument();
  expect(axios.get).not.toHaveBeenCalled();
});
